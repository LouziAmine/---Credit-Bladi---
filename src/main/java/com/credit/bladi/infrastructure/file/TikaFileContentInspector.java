package com.credit.bladi.infrastructure.file;

import com.credit.bladi.service.file.FileContentInspector;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;

@Component
public class TikaFileContentInspector implements FileContentInspector {
    private final Tika tika = new Tika();

    @Override
    public String detectContentType(byte[] content) {
        return tika.detect(content);
    }

    @Override
    public String extensionFor(String contentType) {
        try {
            return MimeTypes.getDefaultMimeTypes().forName(contentType).getExtension();
        } catch (MimeTypeException e) {
            return "";
        }
    }

    @Override
    public boolean isReadableWorkbook(byte[] content) {
        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(content))) {
            return workbook.getNumberOfSheets() > 0;
        } catch (IOException | EncryptedDocumentException | IllegalArgumentException e) {
            return false;
        }
    }
}
