package com.credit.bladi.infrastructure.storage;

import com.credit.bladi.service.workflow.document.DocumentStorage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptedFileDocumentStorage implements DocumentStorage {
    private static final Logger LOGGER = LogManager.getLogger(EncryptedFileDocumentStorage.class);

    private static final byte[] MAGIC = "CBENC1".getBytes(StandardCharsets.US_ASCII);

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int IV_LENGTH = 12;

    private static final int TAG_LENGTH_BITS = 128;

    private static final int KEY_LENGTH_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    private final String basePath;

    private final SecretKey key;

    public EncryptedFileDocumentStorage(@Value("${app.document-storage.base-path}") String basePath,
            @Value("${app.document-storage.encryption-key:}") String base64Key) {
        this.basePath = basePath;
        this.key = toKey(base64Key);
        if (key == null) {
            LOGGER.warn("DOCUMENT_ENCRYPTION_DISABLED no app.document-storage.encryption-key configured, "
                + "documents are stored in clear (acceptable for local development only)");
        }
    }

    private static SecretKey toKey(String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            return null;
        }
        byte[] raw = Base64.getDecoder().decode(base64Key.trim());
        if (raw.length != KEY_LENGTH_BYTES) {
            throw new IllegalStateException("app.document-storage.encryption-key must be 32 bytes (base64-encoded)");
        }
        return new SecretKeySpec(raw, "AES");
    }

    @Override
    public void store(Long demandId, String storedFilename, byte[] content) {
        byte[] payload = key == null ? content : encrypt(storedFilename, content);
        try {
            Path dir = directory(demandId);
            Files.createDirectories(dir);
            Files.write(dir.resolve(storedFilename), payload);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to persist uploaded file to disk", e);
        }
    }

    @Override
    public byte[] load(Long demandId, String storedFilename) {
        byte[] payload;
        try {
            payload = Files.readAllBytes(directory(demandId).resolve(storedFilename));
        } catch (IOException e) {
            throw new UncheckedIOException("Stored document is missing or unreadable: " + storedFilename, e);
        }
        return isEncrypted(payload) ? decrypt(storedFilename, payload) : payload;
    }

    @Override
    public void delete(Long demandId, String storedFilename) {
        try {
            Files.deleteIfExists(directory(demandId).resolve(storedFilename));
        } catch (IOException e) {
            LOGGER.error("DOCUMENT_DELETE_FAILED demandId={} storedFilename={}", demandId, storedFilename, e);
        }
    }

    private Path directory(Long demandId) {
        return Path.of(basePath, "demand-" + demandId);
    }

    private byte[] encrypt(String storedFilename, byte[] content) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            cipher.updateAAD(storedFilename.getBytes(StandardCharsets.UTF_8));
            byte[] ciphertext = cipher.doFinal(content);
            byte[] payload = new byte[MAGIC.length + IV_LENGTH + ciphertext.length];
            System.arraycopy(MAGIC, 0, payload, 0, MAGIC.length);
            System.arraycopy(iv, 0, payload, MAGIC.length, IV_LENGTH);
            System.arraycopy(ciphertext, 0, payload, MAGIC.length + IV_LENGTH, ciphertext.length);
            return payload;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Document encryption failed", e);
        }
    }

    private byte[] decrypt(String storedFilename, byte[] payload) {
        if (key == null) {
            throw new IllegalStateException("Document " + storedFilename + " is encrypted but no key is configured");
        }
        try {
            byte[] iv = Arrays.copyOfRange(payload, MAGIC.length, MAGIC.length + IV_LENGTH);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            cipher.updateAAD(storedFilename.getBytes(StandardCharsets.UTF_8));
            return cipher.doFinal(payload, MAGIC.length + IV_LENGTH, payload.length - MAGIC.length - IV_LENGTH);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Document " + storedFilename + " failed integrity check on decryption", e);
        }
    }

    private static boolean isEncrypted(byte[] payload) {
        return payload.length > MAGIC.length + IV_LENGTH
            && Arrays.equals(payload, 0, MAGIC.length, MAGIC, 0, MAGIC.length);
    }
}
