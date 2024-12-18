package com.example.PersonalAccounting.util.validators.id_match;

import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.services.entity_service_impl.TransactionService;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionUserIdMatch extends UserIdMatch {

    private final TransactionService transactionService;

    @Autowired
    public TransactionUserIdMatch(UserService userService, TransactionService transactionService) {
        super(userService);
        this.transactionService = transactionService;
    }

    @Override
    public void matchUserId(int transactionId) {
        Transaction transaction = transactionService.getOne(transactionId);
        super.matchUserId(transaction.getUser().getId());
    }
}
