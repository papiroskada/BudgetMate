package com.example.PersonalAccounting.controllers;

import com.example.PersonalAccounting.dto.AccumulationDTO;
import com.example.PersonalAccounting.dto.TransactionDTO;
import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.services.entity_service_impl.AccumulationService;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import com.example.PersonalAccounting.util.dto_entity_converter.AccumulationDtoConverter;
import com.example.PersonalAccounting.util.dto_entity_converter.TransactionDTOConverter;
import com.example.PersonalAccounting.util.response.ErrorResponse;
import com.example.PersonalAccounting.util.exceptions.PaymentException;
import com.example.PersonalAccounting.util.validators.AccumulationValidator;
import com.example.PersonalAccounting.util.validators.id_match.AccumulationUserIdMatch;
import jakarta.validation.Valid;
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
@RequestMapping("accumulations")
public class AccumulationController {

    private final AccumulationService accumulationService;
    private final UserService userService;
    private final AccumulationDtoConverter accumulationDtoConverter;
    private final TransactionDTOConverter transactionDTOConverter;
    private final AccumulationUserIdMatch accumulationUserIdMatch;
    private final AccumulationValidator accumulationValidator;

    public AccumulationController(AccumulationService accumulationService, UserService userService,
                                  AccumulationDtoConverter accumulationDtoConverter, TransactionDTOConverter transactionDTOConverter,
                                  AccumulationUserIdMatch accumulationUserIdMatch, AccumulationValidator accumulationValidator) {
        this.accumulationService = accumulationService;
        this.userService = userService;
        this.accumulationDtoConverter = accumulationDtoConverter;
        this.transactionDTOConverter = transactionDTOConverter;
        this.accumulationUserIdMatch = accumulationUserIdMatch;
        this.accumulationValidator = accumulationValidator;
    }

    @GetMapping("/all")
    public List<AccumulationDTO> getAll() {
        return accumulationService.getAll().stream().map(accumulationDtoConverter::convertToAccumulationDTO)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<AccumulationDTO> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getOne(userDetails.getUsername());
        return accumulationService.getAll(user).stream().map(accumulationDtoConverter::convertToAccumulationDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AccumulationDTO getOne(@PathVariable int id) {
        accumulationUserIdMatch.matchUserId(id);
        return accumulationDtoConverter.convertToAccumulationDTO(accumulationService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid AccumulationDTO accumulationDTO,
                                             BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        Accumulation accumulation = accumulationDtoConverter.convertToAccumulation(accumulationDTO);
        accumulation.setUser(userService.getOne(userDetails.getUsername()));

        accumulationValidator.validate(accumulation, bindingResult);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        accumulationService.create(accumulation);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable int id, @RequestBody @Valid AccumulationDTO accumulationDTO,
                                             BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        accumulationUserIdMatch.matchUserId(id);

        Accumulation accumulation = accumulationDtoConverter.convertToAccumulation(accumulationDTO);
        accumulation.setUser(userService.getOne(userDetails.getUsername()));

        accumulationValidator.validate(accumulation, bindingResult);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        accumulationService.update(id, accumulation);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        accumulationUserIdMatch.matchUserId(id);

        accumulationService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<HttpStatus> payment(@PathVariable int id, @RequestBody @Valid TransactionDTO transactionDTO,
                                              BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        accumulationUserIdMatch.matchUserId(id);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        Transaction transaction = transactionDTOConverter.convertToTransaction(transactionDTO);
        transaction.setUser(userService.getOne(userDetails.getUsername()));

        accumulationService.makePayment(id, transaction);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<HttpStatus> close(@PathVariable int id) {
        accumulationUserIdMatch.matchUserId(id);

        accumulationService.closeAccumulation(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PaymentException e) {
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
