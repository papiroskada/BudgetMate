package com.example.PersonalAccounting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FinancialArrangementStatisticsDTO {

    private int creditNum;

    private int depositNum;

    private double percentOfOverdueCredit;

    private int moneySpendOnCreditPayment;

    private int moneyEarningByDepositsInYear;

    private int allDepositMoney;
}
