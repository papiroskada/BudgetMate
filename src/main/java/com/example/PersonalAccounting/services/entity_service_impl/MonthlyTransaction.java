package com.example.PersonalAccounting.services.entity_service_impl;

import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.services.entity_service_impl.TransactionService;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application.properties")
public class MonthlyTransaction {

    private final TransactionService transactionService;

    @Autowired
    public MonthlyTransaction(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @Scheduled(cron = "${transaction.periodic.cron.monthly}")
    protected void periodicTransaction() {
        List<Transaction> transactions = transactionService.getByPeriodic(true);
        if(transactions.isEmpty()) return;

        transactions = transactions.stream()
                .filter(t -> ChronoUnit.MONTHS.between(LocalDate.now(), t.getDateTime()) == -1)
                .collect(Collectors.toList());

        List<Transaction> periodicTransactions = new ArrayList<>();

        for(Transaction transaction: transactions) {
            transaction.setDateTime(null);
            transaction.setId(0);

            if (!periodicTransactions.contains(transaction)) {
                transaction.setDateTime(LocalDateTime.now());
                periodicTransactions.add(transaction);
            }
        }

        transactionService.batchCreate(periodicTransactions);
    }
}
