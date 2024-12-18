package com.example.PersonalAccounting.services.finantial_arrangement_calculations.impl;

import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.enums.FinancialArrangementState;
import com.example.PersonalAccounting.entity.enums.TransactionCategory;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.FinancialArrangementStartEndTransactionCreator;
import org.springframework.stereotype.Component;

@Component
public class CreditStartEndTransactionCreator implements FinancialArrangementStartEndTransactionCreator {

    @Override
    public Transaction createStartTransaction(FinancialArrangement financialArrangement, User user) {
        Transaction transaction = new Transaction();

        if(financialArrangement.isFromToUserFunds()) {
            transaction.setSum(financialArrangement.getStartSum());
            transaction.setUser(user);
            transaction.setRefill(true);
            transaction.setComment("Credit money");
            transaction.setCategory(TransactionCategory.FINANCIAL_SERVICES);
        }
        return transaction;
    }

    @Override
    public Transaction createEndTransaction(FinancialArrangement financialArrangement, User user) {
        return new Transaction();
    }

    @Override
    public FinancialArrangementState operatedState() {
        return FinancialArrangementState.CREDIT;
    }
}
