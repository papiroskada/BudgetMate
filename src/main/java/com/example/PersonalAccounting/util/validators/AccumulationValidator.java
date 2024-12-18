package com.example.PersonalAccounting.util.validators;

import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AccumulationValidator implements Validator {
    private final UserService userService;

    public AccumulationValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Accumulation.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Accumulation accumulation = (Accumulation) target;
        User user = userService.getOne(accumulation.getUser().getId());

        boolean repeatedName = user.getAccumulations().stream()
                .map(Accumulation::getName)
                .anyMatch(n -> n.equals(accumulation.getName()));

        if(repeatedName) {
            errors.rejectValue("name", "400", "You already have accumulation with this name");
        }
    }
}
