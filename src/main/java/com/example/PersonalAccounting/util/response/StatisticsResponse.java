package com.example.PersonalAccounting.util.response;

import com.example.PersonalAccounting.dto.AccumulationStatisticsDTO;
import com.example.PersonalAccounting.dto.FinancialArrangementStatisticsDTO;
import com.example.PersonalAccounting.dto.TransactionStatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {

    private TransactionStatisticsDTO transactionStatistics;
    private AccumulationStatisticsDTO accumulationStatistics;
    private FinancialArrangementStatisticsDTO financialArrangementStatistics;
}
