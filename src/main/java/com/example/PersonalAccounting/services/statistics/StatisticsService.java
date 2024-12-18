package com.example.PersonalAccounting.services.statistics;

import com.example.PersonalAccounting.dto.AccumulationStatisticsDTO;
import com.example.PersonalAccounting.dto.FinancialArrangementStatisticsDTO;
import com.example.PersonalAccounting.dto.TransactionStatisticsDTO;
import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.enums.FinancialArrangementState;
import com.example.PersonalAccounting.entity.enums.Status;
import com.example.PersonalAccounting.entity.enums.TransactionCategory;
import com.example.PersonalAccounting.services.entity_service_impl.AccumulationService;
import com.example.PersonalAccounting.services.entity_service_impl.FinancialArrangementService;
import com.example.PersonalAccounting.services.entity_service_impl.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:application.properties")
public class StatisticsService {

    private final TransactionService transactionService;
    private final AccumulationService accumulationService;
    private final FinancialArrangementService financialArrangementService;

    //TODO: maybe set default value for handler
    private StatisticsFileHandler fileHandler;

    @Autowired
    public StatisticsService(TransactionService transactionService, AccumulationService accumulationService,
                             FinancialArrangementService financialArrangementService) {
        this.transactionService = transactionService;
        this.accumulationService = accumulationService;
        this.financialArrangementService = financialArrangementService;
    }

    @Transactional(readOnly = true)
    public File getStatisticsInFile(User user, LocalDate localDate) {
        if(fileHandler == null)
            throw new NullPointerException("Can't generate statistics file without fileGenerator");

        List<Transaction> transactions = transactionService.getByUserSinceDate(user, localDate);
        List<Accumulation> accumulations = accumulationService.getByUserSinceDate(user, localDate);
        List<FinancialArrangement> arrangements = financialArrangementService.getByUserSinceDate(user, localDate);

        if(transactions.isEmpty() || accumulations.isEmpty() || arrangements.isEmpty())
            throw new IllegalArgumentException("No data for statistics");

        return fileHandler.generateStatisticsFile(transactions, accumulations, arrangements, user.getEmail());
    }

    @Scheduled(cron = "${statistics.file.cron.daily}")
    public void deleteDailyStatisticsFile() throws IOException {
        StatisticsFileHandler.deleteAllFile();
    }

    public TransactionStatisticsDTO getTransactionStatistics(User user, LocalDate startDate) {
        List<Transaction> transactions = transactionService.getByUserSinceDate(user, startDate);

        if(transactions.isEmpty())
            throw new IllegalArgumentException("No data for statistics");

        TransactionStatisticsDTO statisticsDTO = new TransactionStatisticsDTO();

        statisticsDTO.setTransactionNum(transactions.size());
        statisticsDTO.setRefillTransactionSum(
                transactionService.getTransactionSumByUserAndRefill(user, true));
        statisticsDTO.setNonRefillTransactionSum(
                transactionService.getTransactionSumByUserAndRefill(user, false));

        Map<TransactionCategory, Double> percentMap = getCategorySumMap(transactions);
        for(Map.Entry<TransactionCategory, Double> set: percentMap.entrySet()) {
            set.setValue(set.getValue() * 100 / statisticsDTO.getNonRefillTransactionSum());
        }

        statisticsDTO.setCategoryPercent(percentMap);

        return statisticsDTO;
    }

    private Map<TransactionCategory, Double> getCategorySumMap(List<Transaction> transactions) {
        Map<TransactionCategory, List<Transaction>> categoryMap = new HashMap<>();
        Map<TransactionCategory, Double> sumMap = new HashMap<>();

        transactions = transactions.stream().filter(t -> !t.isRefill()).collect(Collectors.toList());
        for (Transaction transaction : transactions) {
            categoryMap.computeIfAbsent(transaction.getCategory(), k -> new ArrayList<>()).add(transaction);
        }

        for(TransactionCategory category: categoryMap.keySet()) {
            double sum = categoryMap.get(category).stream()
                    .mapToDouble(Transaction::getSum).sum();

            sumMap.put(category, sum);
        }
        return sumMap;
    }

    public AccumulationStatisticsDTO getAccumulationStatistics(User user, LocalDate startDate) {
        List<Accumulation> accumulations = accumulationService.getByUserSinceDate(user, startDate);

        if(accumulations.isEmpty())
            throw new IllegalArgumentException("No data for statistics");

        AccumulationStatisticsDTO statisticsDTO = new AccumulationStatisticsDTO();

        statisticsDTO.setAccumulationsNum(accumulations.size());
        statisticsDTO.setPercentOfOverdueAccumulations(accumulations.stream()
                .filter(a -> a.getStatus() == Status.OVERDUE)
                .count() * 100.0 / accumulations.size());
        statisticsDTO.setAccumulatedMoney(getAccumulatedSum(accumulations));

        return statisticsDTO;
    }

    private int getAccumulatedSum(List<Accumulation> accumulations) {
        int sum = 0;
        for(Accumulation accumulation : accumulations) {
            sum += accumulation.getCurrentSum();
            if(accumulation.getStatus() == Status.EXECUTED)
                sum += accumulation.getGoalSum();
        }
        return sum;
    }

    public FinancialArrangementStatisticsDTO getFinancialArrangementStatistics(User user, LocalDate startDate) {
        List<FinancialArrangement> financialArrangements = financialArrangementService.getByUserSinceDate(user, startDate);

        if(financialArrangements.isEmpty())
            throw new IllegalArgumentException("No data for statistics");

        FinancialArrangementStatisticsDTO statisticsDTO = new FinancialArrangementStatisticsDTO();

        fillCreditStatistics(financialArrangements, statisticsDTO);
        fillDepositStatistics(financialArrangements, statisticsDTO, startDate);

        return statisticsDTO;
    }

    private void fillCreditStatistics(List<FinancialArrangement> financialArrangements,
                                      FinancialArrangementStatisticsDTO creditStatisticsDTO) {
        List<FinancialArrangement> credit = financialArrangements.stream()
                .filter(fa -> fa.getState() == FinancialArrangementState.CREDIT)
                .toList();

        if(credit.isEmpty()) return;

        creditStatisticsDTO.setCreditNum(credit.size());
        creditStatisticsDTO.setMoneySpendOnCreditPayment(credit.stream()
                .mapToInt(FinancialArrangement::getCurrentSum).sum());
        creditStatisticsDTO.setPercentOfOverdueCredit(credit.stream()
                .filter(c -> c.getStatus() == Status.OVERDUE)
                .count() * 100.0 / creditStatisticsDTO.getCreditNum());
    }

    private void fillDepositStatistics(List<FinancialArrangement> financialArrangements,
                                       FinancialArrangementStatisticsDTO depositStatisticsDTO, LocalDate startDate) {
        List<FinancialArrangement> deposits = financialArrangements.stream()
                .filter(fa -> fa.getState() == FinancialArrangementState.DEPOSIT)
                .toList();

        if(deposits.isEmpty()) return;

        depositStatisticsDTO.setDepositNum(deposits.size());
        depositStatisticsDTO.setAllDepositMoney(deposits.stream().mapToInt(FinancialArrangement::getCurrentSum).sum());

        int sum = 0;
        for(FinancialArrangement deposit: deposits) {
            sum += deposit.getStartSum() * deposit.getPercent() / 100;
        }

        depositStatisticsDTO.setMoneyEarningByDepositsInYear(sum);
    }

    public StatisticsFileHandler getFileHandler() {
        return fileHandler;
    }

    public void setFileHandler(StatisticsFileHandler fileGenerator) {
        this.fileHandler = fileGenerator;
    }
}
