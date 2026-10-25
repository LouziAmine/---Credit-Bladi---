package com.credit.bladi.batch.excel;

import org.apache.poi.ss.usermodel.Row;

@FunctionalInterface
public interface RowMapper<T> {
    T map(Row row, int rowNumber) throws ImportRowException;
}
