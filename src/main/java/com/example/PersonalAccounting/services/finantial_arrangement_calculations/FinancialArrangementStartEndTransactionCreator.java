package com.example.PersonalAccounting.services.finantial_arrangement_calculations;

import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.User;

public interface FinancialArrangementStartEndTransactionCreator extends StateClass{

    Transaction createStartTransaction(FinancialArrangement financialArrangement, User user);

    Transaction createEndTransaction(FinancialArrangement financialArrangement, User user);
}
