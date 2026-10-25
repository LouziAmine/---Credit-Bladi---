package com.credit.bladi.infrastructure.antivirus;

import com.credit.bladi.service.workflow.document.AntivirusScanner;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.document-storage.antivirus", name = "enabled", havingValue = "true")
public class ClamAvAntivirusScanner implements AntivirusScanner {
    private static final Logger LOGGER = LogManager.getLogger(ClamAvAntivirusScanner.class);

    private static final int CHUNK_SIZE = 8192;

    private static final byte[] INSTREAM_COMMAND = "zINSTREAM\0".getBytes(StandardCharsets.US_ASCII);

    private final String host;

    private final int port;

    private final int timeoutMs;

    public ClamAvAntivirusScanner(
            @Value("${app.document-storage.antivirus.host}") String host,
            @Value("${app.document-storage.antivirus.port}") int port,
            @Value("${app.document-storage.antivirus.timeout-ms:10000}") int timeoutMs) {
        this.host = host;
        this.port = port;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public boolean isInfected(byte[] content) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(timeoutMs);

            OutputStream rawOut = socket.getOutputStream();
            rawOut.write(INSTREAM_COMMAND);
            DataOutputStream out = new DataOutputStream(rawOut);
            int offset = 0;
            while (offset < content.length) {
                int len = Math.min(CHUNK_SIZE, content.length - offset);
                out.writeInt(len);
                out.write(content, offset, len);
                offset += len;
            }
            out.writeInt(0);
            out.flush();
            socket.shutdownOutput();

            InputStream in = socket.getInputStream();
            String response = new String(in.readAllBytes(), StandardCharsets.US_ASCII).trim();
            LOGGER.debug("ClamAV INSTREAM response: {}", response);
            return response.contains("FOUND");
        } catch (IOException e) {
            LOGGER.error("ClamAV scan failed: rejecting upload (fail-closed)", e);
            return true;
        }
    }
}
