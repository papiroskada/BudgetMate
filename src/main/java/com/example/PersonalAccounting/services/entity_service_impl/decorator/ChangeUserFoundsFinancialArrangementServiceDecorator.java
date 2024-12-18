package com.example.PersonalAccounting.services.entity_service_impl.decorator;

import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.enums.FinancialArrangementState;
import com.example.PersonalAccounting.services.entity_service_impl.FinancialArrangementService;
import com.example.PersonalAccounting.services.entity_service_impl.TransactionService;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.FinancialArrangementCalculations;
import com.example.PersonalAccounting.services.finantial_arrangement_calculations.FinancialArrangementStartEndTransactionCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChangeUserFoundsFinancialArrangementServiceDecorator extends AbstractFinancialArrangementServiceDecorator {

    private final List<FinancialArrangementStartEndTransactionCreator> financialArrangementStartEndTransactionCreators;
    private final List<FinancialArrangementCalculations> financialArrangementCalculationsList;
    private final TransactionService transactionService;



    //TODO: make financialArrangementService settable
    @Autowired
    public ChangeUserFoundsFinancialArrangementServiceDecorator(FinancialArrangementService financialArrangementService,
                                                                        List<FinancialArrangementStartEndTransactionCreator> financialArrangementStartEndTransactionCreators,
                                                                        List<FinancialArrangementCalculations> financialArrangementCalculationsList,
                                                                        TransactionService transactionService) {
        super(financialArrangementService);
        this.financialArrangementStartEndTransactionCreators = financialArrangementStartEndTransactionCreators;
        this.financialArrangementCalculationsList = financialArrangementCalculationsList;
        this.transactionService = transactionService;
    }

    @Override
    public FinancialArrangement create(FinancialArrangement toCreate) {
        FinancialArrangement financialArrangement = super.create(toCreate);

        if(toCreate.isFromToUserFunds()) {
            Transaction transaction =
                    getTransactionCreatorByState(toCreate.getState()).createStartTransaction(toCreate, toCreate.getUser());
            transactionService.create(transaction);
        }

        return financialArrangement;
    }

    @Override
    public FinancialArrangement makePayment(int id) {
        FinancialArrangement financialArrangement = super.makePayment(id);

        FinancialArrangementCalculations calculations = getCalculationsByState(financialArrangement.getState());

        FinancialArrangementStartEndTransactionCreator transactionCreator =
                getTransactionCreatorByState(financialArrangement.getState());

        if(calculations.isFullyRepaid(financialArrangement)) {
            Transaction endTransaction =
                    transactionCreator.createEndTransaction(financialArrangement, financialArrangement.getUser());
            if(!endTransaction.isEmpty())
                transactionService.create(endTransaction);
        }
        return financialArrangement;
    }


    private FinancialArrangementStartEndTransactionCreator getTransactionCreatorByState(FinancialArrangementState state)
            throws IllegalArgumentException{
        return financialArrangementStartEndTransactionCreators.stream()
                .filter(c -> c.operatedState() != null)
                .filter(c -> c.operatedState().equals(state))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such state"));
    }

    private FinancialArrangementCalculations getCalculationsByState(FinancialArrangementState state)
            throws IllegalArgumentException{
        return financialArrangementCalculationsList.stream()
                .filter(c -> c.operatedState() != null)
                .filter(c -> c.operatedState().equals(state))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such state"));
    }
}
