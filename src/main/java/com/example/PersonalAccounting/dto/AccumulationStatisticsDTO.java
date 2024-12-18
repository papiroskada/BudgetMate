package com.example.PersonalAccounting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccumulationStatisticsDTO {

    private int accumulationsNum;

    private int accumulatedMoney;

    private double percentOfOverdueAccumulations;
}
