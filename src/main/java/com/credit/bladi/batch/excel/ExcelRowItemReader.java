package com.credit.bladi.batch.excel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamException;
import org.springframework.batch.infrastructure.item.ItemStreamReader;

public class ExcelRowItemReader<T> implements ItemStreamReader<T> {
    private final Path path;
    private final RowMapper<T> rowMapper;

    private Workbook workbook;
    private Iterator<Row> rows;
    private int rowNumber;

    public ExcelRowItemReader(Path path, RowMapper<T> rowMapper) {
        this.path = path;
        this.rowMapper = rowMapper;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try (InputStream in = Files.newInputStream(path)) {
            workbook = WorkbookFactory.create(in);
        } catch (IOException e) {
            throw new ItemStreamException("Failed to open Excel file: " + path, e);
        }
        Iterator<Row> iterator = workbook.getSheetAt(0).iterator();
        if (iterator.hasNext()) {
            iterator.next();
        }
        rows = iterator;
        rowNumber = 1;
    }

    @Override
    public T read() throws Exception {
        while (rows.hasNext()) {
            Row row = rows.next();
            rowNumber++;
            if (isBlank(row)) {
                continue;
            }
            return rowMapper.map(row, rowNumber);
        }
        return null;
    }

    @Override
    public void close() throws ItemStreamException {
        if (workbook == null) {
            return;
        }
        try {
            workbook.close();
        } catch (IOException e) {
            throw new ItemStreamException("Failed to close Excel file: " + path, e);
        }
    }

    private static boolean isBlank(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
