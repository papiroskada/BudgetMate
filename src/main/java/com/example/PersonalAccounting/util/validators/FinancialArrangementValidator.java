package com.example.PersonalAccounting.util.validators;

import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.enums.FinancialArrangementState;
import com.example.PersonalAccounting.services.entity_service_impl.FinancialArrangementService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class FinancialArrangementValidator implements Validator {
    private final FinancialArrangementService financialArrangementService;

    public FinancialArrangementValidator(FinancialArrangementService financialArrangementService) {
        this.financialArrangementService = financialArrangementService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(FinancialArrangement.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FinancialArrangement financialArrangement = (FinancialArrangement) target;
        List<FinancialArrangement> financialArrangements = financialArrangementService.getAll(financialArrangement.getUser());

        boolean repeatedName;

        if(financialArrangement.getState() == FinancialArrangementState.CREDIT) {
            repeatedName = financialArrangements.stream()
                    .filter(fa -> fa.getState() == FinancialArrangementState.DEPOSIT)
                    .map(FinancialArrangement::getName)
                    .anyMatch(n -> n.equals(financialArrangement.getName()));
        }else {
            repeatedName = financialArrangements.stream()
                    .filter(fa -> fa.getState() == FinancialArrangementState.CREDIT)
                    .map(FinancialArrangement::getName)
                    .anyMatch(n -> n.equals(financialArrangement.getName()));
        }

        if (repeatedName) {
            errors.rejectValue("name", "400", "You already have financial arrangements with this name");
        }
    }
}

