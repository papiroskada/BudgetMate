package com.example.PersonalAccounting.controllers;

import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import com.example.PersonalAccounting.services.statistics.ExcelStatisticsFileHandler;
import com.example.PersonalAccounting.services.statistics.StatisticsFileHandler;
import com.example.PersonalAccounting.services.statistics.StatisticsService;
import com.example.PersonalAccounting.services.statistics.WordStatisticsFileHandler;
import com.example.PersonalAccounting.util.response.ErrorResponse;
import com.example.PersonalAccounting.util.response.StatisticsResponse;
import com.example.PersonalAccounting.util.validators.StatisticsStartDateValidator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final UserService userService;
    private final StatisticsStartDateValidator dateValidator;

    public StatisticsController(StatisticsService statisticsService, UserService userService,
                                StatisticsStartDateValidator dateValidator) {
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.dateValidator = dateValidator;
    }

    @GetMapping
    public ResponseEntity<StatisticsResponse> statistics(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        dateValidator.validate(startDate);

        User user = userService.getOne(userDetails.getUsername());

        StatisticsResponse statisticsResponse = new StatisticsResponse();
        statisticsResponse.setTransactionStatistics(statisticsService.getTransactionStatistics(user, startDate));
        statisticsResponse.setAccumulationStatistics(statisticsService.getAccumulationStatistics(user, startDate));
        statisticsResponse.setFinancialArrangementStatistics(statisticsService.getFinancialArrangementStatistics(user, startDate));

        return ResponseEntity.ok(statisticsResponse);
    }


    //TODO: add start date
    @GetMapping("/file")
    public ResponseEntity<Resource> statisticsFile(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam(name = "file-format", required = false, defaultValue = "xlsx") String format,
                                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        dateValidator.validate(startDate);

        statisticsService.setFileHandler(getHandler(format));

        File file = statisticsService.getStatisticsInFile(userService.getOne(userDetails.getUsername()), startDate);

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Statistics." + format);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Something went wrong. Try later.");
        }
    }

    //TODO: make method get handle format in fileHandler
    private StatisticsFileHandler getHandler(String format) {
        return switch (format) {
            case "xlsx" -> new ExcelStatisticsFileHandler();
            case "docx" -> new WordStatisticsFileHandler();
            default -> throw new IllegalArgumentException("Unsupported format");
        };
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
