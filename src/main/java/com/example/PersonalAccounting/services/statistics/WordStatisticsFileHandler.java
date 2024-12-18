package com.example.PersonalAccounting.services.statistics;

import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WordStatisticsFileHandler extends StatisticsFileHandler {

    //TODO: make another date time format

    @Override
    public File generateStatisticsFile(List<Transaction> transactions, List<Accumulation> accumulations,
                                       List<FinancialArrangement> financialArrangements, String userEmail){
        File statisticsFile = new File(String.format(FILE_PATH, userEmail, "docx"));

        try(FileOutputStream output = new FileOutputStream(statisticsFile)) {
            XWPFDocument document = new XWPFDocument();

            createDocumentStructure(document, transactions, accumulations, financialArrangements);

            List<XWPFTable> tables = document.getTables();

            fillTransactionTable(tables.get(0), transactions);
            fillAccumulationsTable(tables.get(1), accumulations);
            fillFinancialArrangementTable(tables.get(2), financialArrangements);

            document.write(output);
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return statisticsFile;
    }

    protected void createDocumentStructure(XWPFDocument document, List<Transaction> transactions,
                                           List<Accumulation> accumulations, List<FinancialArrangement> financialArrangements){
        document.createTable(transactions.size() + 1, 6);
        addNewLine(document);
        document.createTable(accumulations.size() + 1, 9);
        addNewLine(document);
        document.createTable(financialArrangements.size() + 1, 11);
        addNewLine(document);
    }

    private void fillTransactionTable(XWPFTable table, List<Transaction> transactions) {
        fillTableHead(table.getRow(0).getTableCells(), TRANSACTION_TABLE_COLUMNS_NAME);

        for (int i = 1; i < table.getNumberOfRows(); i++) {
            Transaction transaction = transactions.get(i - 1);
            List<XWPFTableCell> cells = table.getRow(i).getTableCells();

            cells.get(0).setText(i + "");
            cells.get(1).setText(transaction.getSum() + "");
            cells.get(2).setText(transaction.isRefill() + "");
            cells.get(3).setText(transaction.getComment());
            cells.get(4).setText(transaction.getCategory() + "");
            cells.get(5).setText(transaction.getDateTime() + "");
        }
    }

    private void fillAccumulationsTable(XWPFTable table, List<Accumulation> accumulations) {
        fillTableHead(table.getRow(0).getTableCells(), ACCUMULATIONS_TABLE_COLUMNS_NAME);

        for(int i = 1; i < table.getNumberOfRows(); i++) {
            Accumulation accumulation = accumulations.get(i - 1);
            List<XWPFTableCell> cells = table.getRow(i).getTableCells();

            cells.get(0).setText(i + "");
            cells.get(1).setText(accumulation.getName());
            cells.get(2).setText(accumulation.getComment());
            cells.get(3).setText(accumulation.getCurrentSum() + "");
            cells.get(4).setText(accumulation.getGoalSum() + "");
            cells.get(5).setText(accumulation.getStartDate() + "");
            cells.get(6).setText(accumulation.getEndDate() + "");
            cells.get(7).setText(accumulation.getLastPaymentDate() + "");
            cells.get(8).setText(accumulation.getStatus() + "");
        }
    }

    private void fillFinancialArrangementTable(XWPFTable table, List<FinancialArrangement> arrangements) {
        fillTableHead(table.getRow(0).getTableCells(), FINANCIAL_ARRANGEMENT_TABLE_COLUMNS_NAME);

        for(int i = 1; i < table.getNumberOfRows(); i++) {
            FinancialArrangement arrangement = arrangements.get(i - 1);
            List<XWPFTableCell> cells = table.getRow(i).getTableCells();

            cells.get(0).setText(i + "");
            cells.get(1).setText(arrangement.getName());
            cells.get(2).setText(arrangement.getState() + "");
            cells.get(3).setText(arrangement.getPercent() + "%");
            cells.get(4).setText(arrangement.getStartSum() + "");
            cells.get(5).setText(arrangement.getCurrentSum() + "");
            cells.get(6).setText(arrangement.getRefundSum() + "");
            cells.get(7).setText(arrangement.getStartDate() + "");
            cells.get(8).setText(arrangement.getEndDate() + "");
            cells.get(9).setText(arrangement.isFromToUserFunds() + "");
            cells.get(10).setText(arrangement.getStatus() + "");
        }
    }

    private void fillTableHead(List<XWPFTableCell> cells, List<String> columnName){
        for(int i = 0; i < cells.size(); i++) {
            cells.get(i).setText(columnName.get(i));
        }
    }

    private void addNewLine(XWPFDocument document) {
        var paragraph = document.createParagraph();
        var run = paragraph.createRun();
        run.addBreak();
    }
}
