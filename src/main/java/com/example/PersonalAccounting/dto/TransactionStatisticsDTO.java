package com.example.PersonalAccounting.dto;

import com.example.PersonalAccounting.entity.enums.TransactionCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class TransactionStatisticsDTO {

    private Map<TransactionCategory, Double> categoryPercent;

    private int transactionNum;

    private int RefillTransactionSum;

    private int NonRefillTransactionSum;
}
