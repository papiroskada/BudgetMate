package com.example.PersonalAccounting.util;

import org.springframework.validation.FieldError;

import java.util.List;

public class ErrorsUtil {
    public static String generateErrorMessage(List<FieldError> errorList) {
        StringBuilder message = new StringBuilder();

        for(FieldError error: errorList) {
            message
                    .append("Error caused by field ")
                    .append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage() == null ? error.getCode() : error.getDefaultMessage())
                    .append(";");
        }

        return message.toString();
    }
}
