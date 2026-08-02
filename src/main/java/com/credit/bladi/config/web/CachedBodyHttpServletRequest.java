package com.credit.bladi.config.web;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(final HttpServletRequest request, final int maxBodySizeBytes)
            throws IOException {
        super(request);
        this.cachedBody = readBounded(request.getInputStream(), maxBodySizeBytes);
    }

    private static byte[] readBounded(final InputStream in, final int maxBodySizeBytes) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream(Math.min(maxBodySizeBytes, 8192));
        final byte[] chunk = new byte[8192];
        int total = 0;
        int read;
        while ((read = in.read(chunk)) != -1) {
            total += read;
            if (total > maxBodySizeBytes) {
                throw new RequestBodyTooLargeException(maxBodySizeBytes);
            }
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(final ReadListener readListener) {
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        final Charset charset = getCharacterEncoding() == null
            ? StandardCharsets.UTF_8
            : Charset.forName(getCharacterEncoding());
        return new BufferedReader(new InputStreamReader(getInputStream(), charset));
    }
}