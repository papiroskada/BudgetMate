package com.example.PersonalAccounting.services.statistics;

import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelStatisticsFileHandler extends StatisticsFileHandler{

    @Override
    public File generateStatisticsFile(List<Transaction> transactions, List<Accumulation> accumulations,
                                       List<FinancialArrangement> financialArrangements, String userEmail){
        File statisticsFile = new File(String.format(FILE_PATH, userEmail, "xlsx"));

        try(FileOutputStream output = new FileOutputStream(statisticsFile)) {
            Workbook inWorkbook  = new XSSFWorkbook();
            Sheet sheet = inWorkbook.createSheet();
            int startRowIndex = 0;
            int startCellIndex = 0;

            initializeTables(sheet, transactions, accumulations, financialArrangements);
            startCellIndex = fillTransactionTable(sheet, startRowIndex, startCellIndex, transactions);
            startCellIndex = fillAccumulationsTable(sheet, startRowIndex, startCellIndex, accumulations);
            fillFinancialArrangementTable(sheet, startRowIndex, startCellIndex, financialArrangements);

            inWorkbook.write(output);
            inWorkbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return statisticsFile;
    }

    private void initializeTables(Sheet sheet, List<Transaction> transactions, List<Accumulation> accumulations,
                                  List<FinancialArrangement> financialArrangements) {
        int rows = Math.max(Math.max(transactions.size(), accumulations.size()), financialArrangements.size());
        int cells = 29;
        for(int i = 0; i < rows; i++) {
            Row row = sheet.createRow(i);
            for(int j = 0; j < cells; j++) {
                row.createCell(j);
            }
        }

    }

    private int fillTransactionTable(Sheet sheet, int startRowIndex, int startCellIndex, List<Transaction> transactions) {
        fillTableHead(sheet.getRow(startRowIndex), startCellIndex, TRANSACTION_TABLE_COLUMNS_NAME);

        for(int i = 1; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i - 1);
            Row currentRow = sheet.getRow(startRowIndex + i);

            currentRow.getCell(startCellIndex).setCellValue(i);
            currentRow.getCell(startCellIndex + 1).setCellValue(transaction.getSum());
            currentRow.getCell(startCellIndex + 2).setCellValue(transaction.isRefill());
            currentRow.getCell(startCellIndex + 3).setCellValue(transaction.getComment());
            currentRow.getCell(startCellIndex + 4).setCellValue(transaction.getCategory() + "");
            currentRow.getCell(startCellIndex + 5).setCellValue(transaction.getDateTime());
        }

        return startCellIndex + 7;
    }



    private int fillAccumulationsTable(Sheet sheet, int startRowIndex, int startCellIndex, List<Accumulation> accumulations) {
        fillTableHead(sheet.getRow(startRowIndex), startCellIndex, ACCUMULATIONS_TABLE_COLUMNS_NAME);

        for(int i = 1; i < accumulations.size(); i++) {
            Accumulation accumulation = accumulations.get(i - 1);
            Row currentRow = sheet.getRow(startRowIndex + i);

            currentRow.getCell(startCellIndex).setCellValue(i);
            currentRow.getCell(startCellIndex + 1).setCellValue(accumulation.getName());
            currentRow.getCell(startCellIndex + 2).setCellValue(accumulation.getComment());
            currentRow.getCell(startCellIndex + 3).setCellValue(accumulation.getCurrentSum());
            currentRow.getCell(startCellIndex + 4).setCellValue(accumulation.getGoalSum());
            currentRow.getCell(startCellIndex + 5).setCellValue(accumulation.getStartDate());
            currentRow.getCell(startCellIndex + 6).setCellValue(accumulation.getEndDate());
            currentRow.getCell(startCellIndex + 7).setCellValue(accumulation.getLastPaymentDate());
            currentRow.getCell(startCellIndex + 8).setCellValue(accumulation.getStatus() + "");
        }

        return startCellIndex + 10;
    }

    private int fillFinancialArrangementTable(Sheet sheet, int startRowIndex, int startCellIndex,
                                               List<FinancialArrangement> arrangements) {
        fillTableHead(sheet.getRow(startRowIndex), startCellIndex, FINANCIAL_ARRANGEMENT_TABLE_COLUMNS_NAME);

        for(int i = 1; i < arrangements.size(); i++) {
            FinancialArrangement arrangement = arrangements.get(i - 1);
            Row currentRow = sheet.getRow(startRowIndex + i);

            currentRow.getCell(startCellIndex).setCellValue(i);
            currentRow.getCell(startCellIndex + 1).setCellValue(arrangement.getName());
            currentRow.getCell(startCellIndex + 2).setCellValue(arrangement.getState() + "");
            currentRow.getCell(startCellIndex + 3).setCellValue(arrangement.getPercent() + "%");
            currentRow.getCell(startCellIndex + 4).setCellValue(arrangement.getStartSum());
            currentRow.getCell(startCellIndex + 5).setCellValue(arrangement.getCurrentSum());
            currentRow.getCell(startCellIndex + 6).setCellValue(arrangement.getRefundSum());
            currentRow.getCell(startCellIndex + 7).setCellValue(arrangement.getStartDate());
            currentRow.getCell(startCellIndex + 8).setCellValue(arrangement.getEndDate());
            currentRow.getCell(startCellIndex + 9).setCellValue(arrangement.isFromToUserFunds());
            currentRow.getCell(startCellIndex + 10).setCellValue(arrangement.getStatus() + "");
        }
        return startCellIndex + 12;
    }

    private void fillTableHead(Row row, int startCellIndex, List<String> columnName){
        for(int i = 0; i < columnName.size(); i++) {
            row.getCell(startCellIndex + i).setCellValue(columnName.get(i));
        }
    }
}
