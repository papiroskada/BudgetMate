package com.example.PersonalAccounting.util.validators;

import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PasswordMatch {

    private final UserService userService;

    @Autowired
    public PasswordMatch(UserService userService) {
        this.userService = userService;
    }

    public void match(String checked, String password, Errors errors) {
        if(!userService.matchPassword(checked, password)) {
            errors.rejectValue("oldPassword", "400", "Wrong password");
        }
    }
}
