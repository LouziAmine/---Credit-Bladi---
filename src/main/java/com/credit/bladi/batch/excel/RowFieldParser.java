package com.credit.bladi.batch.excel;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.MessageSource;

public class RowFieldParser {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final MessageSource messageSource;

    public RowFieldParser(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String message(String key, Object... args) {
        return messageSource.getMessage(key, args, Locale.FRENCH);
    }

    public String requiredString(Row row, int rowNumber, int column, String fieldName) {
        String value = cellText(row, rowNumber, column);
        if (value.isBlank()) {
            throw new ImportRowException(rowNumber, message("demandImport.fieldRequired", fieldName));
        }
        return value;
    }

    public String optionalString(Row row, int rowNumber, int column) {
        String value = cellText(row, rowNumber, column);
        return value.isBlank() ? null : value;
    }

    public String requiredEmail(Row row, int rowNumber, int column, String fieldName) {
        String email = requiredString(row, rowNumber, column, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ImportRowException(rowNumber, message("demandImport.contactEmailInvalid", email));
        }
        return email;
    }

    public BigDecimal requiredDecimal(Row row, int rowNumber, int column, String fieldName) {
        BigDecimal value = optionalDecimal(row, rowNumber, column);
        if (value == null) {
            throw new ImportRowException(rowNumber, message("demandImport.fieldRequiredNumeric", fieldName));
        }
        return value;
    }

    public BigDecimal optionalDecimal(Row row, int rowNumber, int column) {
        String text = cellText(row, rowNumber, column);
        if (text.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new ImportRowException(rowNumber, message("demandImport.invalidNumericValue", text));
        }
    }

    public Integer requiredInt(Row row, int rowNumber, int column, String fieldName) {
        Integer value = optionalInt(row, rowNumber, column, fieldName);
        if (value == null) {
            throw new ImportRowException(rowNumber, message("demandImport.fieldRequiredInteger", fieldName));
        }
        return value;
    }

    public Integer optionalInt(Row row, int rowNumber, int column, String fieldName) {
        String text = cellText(row, rowNumber, column);
        if (text.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(text).intValueExact();
        } catch (ArithmeticException | NumberFormatException e) {
            throw new ImportRowException(rowNumber, message("demandImport.fieldInvalidInteger", fieldName, text));
        }
    }

    private String cellText(Row row, int rowNumber, int column) {
        Cell cell = row.getCell(column);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> numericText(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> formulaResultText(cell, rowNumber, column);
            case ERROR -> throw errorCellException(rowNumber, column);
            default -> cell.getStringCellValue().trim();
        };
    }

    private String formulaResultText(Cell cell, int rowNumber, int column) {
        return switch (cell.getCachedFormulaResultType()) {
            case NUMERIC -> numericText(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case ERROR -> throw errorCellException(rowNumber, column);
            case BLANK -> "";
            default -> cell.getStringCellValue().trim();
        };
    }

    private static String numericText(double numeric) {
        return numeric == Math.floor(numeric) && !Double.isInfinite(numeric)
            ? String.valueOf((long) numeric)
            : BigDecimal.valueOf(numeric).toPlainString();
    }

    private ImportRowException errorCellException(int rowNumber, int column) {
        return new ImportRowException(rowNumber, message("demandImport.excelErrorCell", column));
    }
}
