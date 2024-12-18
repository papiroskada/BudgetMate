package com.example.PersonalAccounting.util.validators;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.xml.validation.Validator;
import java.time.LocalDate;

@Component
public class StatisticsStartDateValidator {

    public void validate(LocalDate localDate) {
        if(localDate.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Date should be in past");
    }
}
