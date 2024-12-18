package com.example.PersonalAccounting.services.finantial_arrangement_calculations.impl;

import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.enums.FinancialArrangementState;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.FinancialArrangementCalculations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DepositCalculations implements FinancialArrangementCalculations {

    @Override
    public void makePayment(FinancialArrangement arrangement) {
        arrangement.setCurrentSum(arrangement.getCurrentSum() + calculatePaymentSumByTimeLine(arrangement));
    }

    @Override
    public int calculateCurrentSumInitValue(FinancialArrangement arrangement) {
        return arrangement.getStartSum();
    }

    @Override
    public int calculateRefundSum(FinancialArrangement arrangement) {
        int onceProfit = arrangement.getStartSum() * arrangement.getPercent() / 100;
        int allProfit = onceProfit * timeLineBetweenDates(
                arrangement.getStartDate(), arrangement.getEndDate());

        return arrangement.getStartSum() + allProfit;
    }

    @Override
    public int calculatePaymentSumByTimeLine(FinancialArrangement arrangement) {
        int paymentsNum = timeLineBetweenDates(
                arrangement.getStartDate(), arrangement.getEndDate());
        int ceilSum = (int) Math.ceil(calculateRefundSum(arrangement) * 1.0 / paymentsNum);

        return Math.min(ceilSum, calculateRefundSum(arrangement) - arrangement.getCurrentSum());
    }

    @Override
    public boolean isOutOfDate(FinancialArrangement arrangement) {
        int requirePayments = timeLineBetweenDates(arrangement.getStartDate(), LocalDate.now());
        if(requirePayments == 0) return false;

        int profit = arrangement.getCurrentSum() - arrangement.getStartSum();
        int madePayments = profit / calculatePaymentSumByTimeLine(arrangement);

        return !(madePayments >= requirePayments);
    }

    @Override
    public boolean isFullyRepaid(FinancialArrangement arrangement) {
        return arrangement.getCurrentSum() == calculateRefundSum(arrangement);
    }

    @Override
    public int timeLineBetweenDates(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.YEARS.between(start, end);
    }

    @Override
    public FinancialArrangementState operatedState() {
        return FinancialArrangementState.DEPOSIT;
    }

    @Override
    public Transaction createPaymentTransaction(FinancialArrangement arrangement, User user) {
        return new Transaction();
    }


}
