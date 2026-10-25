package com.credit.bladi.batch.excel;

public class ImportRowException extends RuntimeException {
    private final int rowNumber;

    public ImportRowException(int rowNumber, String message) {
        super(message);
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
