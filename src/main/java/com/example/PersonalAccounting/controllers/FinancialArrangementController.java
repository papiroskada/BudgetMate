package com.example.PersonalAccounting.controllers;

import com.example.PersonalAccounting.dto.FinancialArrangementDTO;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.services.entity_service_impl.FinancialArrangementService;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import com.example.PersonalAccounting.services.entity_service_impl.decorator.ChangeUserFoundsFinancialArrangementServiceDecorator;
import com.example.PersonalAccounting.util.dto_entity_converter.FinancialArrangementDtoConverter;
import com.example.PersonalAccounting.util.response.ErrorResponse;
import com.example.PersonalAccounting.util.exceptions.PaymentException;
import com.example.PersonalAccounting.util.validators.FinancialArrangementValidator;
import com.example.PersonalAccounting.util.validators.id_match.FinancialArrangementUserIdMatch;
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

//TODO: try to do something with error handler & converter (to many same code)

@RestController
@RequestMapping("/financial-arrangements")
public class FinancialArrangementController {

    private final FinancialArrangementService financialArrangementService;
    private final ChangeUserFoundsFinancialArrangementServiceDecorator userFoundsServiceDecorator;
    private final UserService userService;
    private final FinancialArrangementUserIdMatch financialArrangementUserIdMatch;
    private final FinancialArrangementDtoConverter financialArrangementDtoConverter;
    private final FinancialArrangementValidator financialArrangementValidator;

    @Autowired
    public FinancialArrangementController(FinancialArrangementService financialArrangementService,
                                          ChangeUserFoundsFinancialArrangementServiceDecorator userFoundsServiceDecorator,
                                          UserService userService, FinancialArrangementUserIdMatch financialArrangementUserIdMatch,
                                          FinancialArrangementDtoConverter financialArrangementDtoConverter,
                                          FinancialArrangementValidator financialArrangementValidator) {
        this.financialArrangementService = financialArrangementService;
        this.userFoundsServiceDecorator = userFoundsServiceDecorator;
        this.userService = userService;
        this.financialArrangementUserIdMatch = financialArrangementUserIdMatch;
        this.financialArrangementDtoConverter = financialArrangementDtoConverter;
        this.financialArrangementValidator = financialArrangementValidator;
    }

    @GetMapping("/all")
    public List<FinancialArrangementDTO> getAll() {
        return financialArrangementService.getAll().stream().map(financialArrangementDtoConverter::convertToFADTO)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<FinancialArrangementDTO> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getOne(userDetails.getUsername());
        return financialArrangementService.getAll(user).stream().map(financialArrangementDtoConverter::convertToFADTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid FinancialArrangementDTO faDTO, BindingResult bindingResult,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        FinancialArrangement fa = financialArrangementDtoConverter.convertToFA(faDTO);
        fa.setUser(userService.getOne(userDetails.getUsername()));

        financialArrangementValidator.validate(fa, bindingResult);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        if(fa.isFromToUserFunds()) userFoundsServiceDecorator.create(fa);
        else financialArrangementService.create(fa);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable int id, @RequestBody @Valid FinancialArrangementDTO faDTO,
                                             BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        financialArrangementUserIdMatch.matchUserId(id);

        FinancialArrangement fa = financialArrangementDtoConverter.convertToFA(faDTO);
        fa.setUser(userService.getOne(userDetails.getUsername()));

        financialArrangementValidator.validate(fa, bindingResult);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateErrorMessage(bindingResult.getFieldErrors()));
        }

        financialArrangementService.update(id, fa);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
        financialArrangementUserIdMatch.matchUserId(id);
        financialArrangementService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<HttpStatus> payment(@PathVariable int id) {

        financialArrangementUserIdMatch.matchUserId(id);

        FinancialArrangement fa = financialArrangementService.getOne(id);

        if(fa.isFromToUserFunds()) userFoundsServiceDecorator.makePayment(id);
        else financialArrangementService.makePayment(id);

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
