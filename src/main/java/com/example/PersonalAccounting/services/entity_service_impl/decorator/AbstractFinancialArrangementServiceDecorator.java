package com.example.PersonalAccounting.services.entity_service_impl.decorator;

import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.services.CrudService;
import com.example.PersonalAccounting.services.entity_service_impl.FinancialArrangementService;
import com.example.PersonalAccounting.util.exceptions.PaymentException;


public abstract class AbstractFinancialArrangementServiceDecorator extends AbstractCrudServiceDecorator<FinancialArrangement>{

    protected AbstractFinancialArrangementServiceDecorator(CrudService<FinancialArrangement> service) {
        super(service);
    }

    public FinancialArrangement makePayment(int id) {
        if(service instanceof FinancialArrangementService faService) {
            return faService.makePayment(id);
        }else {
            throw new PaymentException("Wrong service. Can't make payment");
        }
    }
}
