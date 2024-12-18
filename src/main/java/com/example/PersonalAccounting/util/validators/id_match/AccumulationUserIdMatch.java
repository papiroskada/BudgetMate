package com.example.PersonalAccounting.util.validators.id_match;

import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.services.entity_service_impl.AccumulationService;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccumulationUserIdMatch extends UserIdMatch {

    private final AccumulationService accumulationService;

    @Autowired
    public AccumulationUserIdMatch(UserService userService, AccumulationService accumulationService) {
        super(userService);
        this.accumulationService = accumulationService;
    }

    @Override
    public void matchUserId(int accumulationId) {
        Accumulation accumulation = accumulationService.getOne(accumulationId);
        super.matchUserId(accumulation.getUser().getId());
    }
}
