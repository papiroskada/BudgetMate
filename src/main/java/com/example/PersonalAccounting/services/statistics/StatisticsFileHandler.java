package com.example.PersonalAccounting.services.statistics;

import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class StatisticsFileHandler{

    private static final String FOLDER = "statistics_file/";

    protected static final String FILE_PATH = "statistics_file/%s.%s";

    protected static final List<String> TRANSACTION_TABLE_COLUMNS_NAME =
            List.of("№", "Sum", "Refill", "Comment", "Category", "Date and time");

    protected static final List<String> ACCUMULATIONS_TABLE_COLUMNS_NAME =
            List.of("№", "Name", "Comment", "Current sum", "Goal sum", "Start date", "End date",
                    "Last payment date", "Status");

    protected static final List<String> FINANCIAL_ARRANGEMENT_TABLE_COLUMNS_NAME =
            List.of("№", "Name", "Type", "Percent", "StartSum", "Current Sum",
                    "Refund Sum", "Start date", "End date", "From to user funds", "Status");


    public abstract File generateStatisticsFile(List<Transaction> transactions, List<Accumulation> accumulations,
                                List<FinancialArrangement> financialArrangements, String userEmail);

    public static void deleteAllFile() {
        try {
            FileUtils.cleanDirectory(new File(FOLDER));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
