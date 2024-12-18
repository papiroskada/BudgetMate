package com.example.PersonalAccounting.util.validators.id_match;

import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.services.entity_service_impl.FinancialArrangementService;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FinancialArrangementUserIdMatch extends UserIdMatch {

    private final FinancialArrangementService financialArrangementService;

    @Autowired
    public FinancialArrangementUserIdMatch(UserService userService, FinancialArrangementService financialArrangementService) {
        super(userService);
        this.financialArrangementService = financialArrangementService;
    }

    @Override
    public void matchUserId(int financialArrangementId) {
        FinancialArrangement financialArrangement = financialArrangementService.getOne(financialArrangementId);
        super.matchUserId(financialArrangement.getUser().getId());
    }
}
