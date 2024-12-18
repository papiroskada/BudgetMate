package com.example.PersonalAccounting.controllers;

import com.example.PersonalAccounting.dto.TransactionDTO;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.services.entity_service_impl.TransactionService;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import com.example.PersonalAccounting.util.dto_entity_converter.TransactionDTOConverter;
import com.example.PersonalAccounting.util.response.ErrorResponse;
import com.example.PersonalAccounting.util.validators.id_match.TransactionUserIdMatch;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.example.PersonalAccounting.util.ErrorsUtil.generateErrorMessage;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final TransactionDTOConverter transactionDTOConverter;
    private final TransactionUserIdMatch transactionUserIdMatch;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService,
                                 TransactionDTOConverter transactionDTOConverter, TransactionUserIdMatch transactionUserIdMatch) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.transactionDTOConverter = transactionDTOConverter;
        this.transactionUserIdMatch = transactionUserIdMatch;
    }

    @GetMapping("/all")
    public List<TransactionDTO> getAll() {
        return transactionService.getAll().stream().map(transactionDTOConverter::convertToTransactionDTO).collect(Collectors.toList());
    }

    @GetMapping
    public List<TransactionDTO> getAllForUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getOne(userDetails.getUsername());
        return transactionService.getAll(user).stream().map(transactionDTOConverter::convertToTransactionDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TransactionDTO getOne(@PathVariable int id) {
        transactionUserIdMatch.matchUserId(id);
        return transactionDTOConverter.convertToTransactionDTO(transactionService.getOne(id));
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid TransactionDTO transactionDTO,
                                             BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        //TODO: maybe add util class where userDetails will be parsed in UserDetailsImpl(if I do this i can take user from it)
        Transaction transaction = transactionDTOConverter.convertToTransaction(transactionDTO);
        transaction.setUser(userService.getOne(userDetails.getUsername()));

        transactionService.create(transaction);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable int id, @RequestBody @Valid TransactionDTO transactionDTO,
                                             BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        transactionUserIdMatch.matchUserId(id);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        Transaction transaction = transactionDTOConverter.convertToTransaction(transactionDTO);
        transaction.setUser(userService.getOne(userDetails.getUsername()));

        transactionService.update(id, transaction);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        transactionUserIdMatch.matchUserId(id);
        transactionService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
