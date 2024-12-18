package com.example.PersonalAccounting.util.validators;

import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.NoSuchElementException;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        try{
            userService.getOne(user.getEmail());
            errors.rejectValue("email", "400", "User with this email is already exist.");
        }catch (NoSuchElementException ignored) {
        }
    }
}
