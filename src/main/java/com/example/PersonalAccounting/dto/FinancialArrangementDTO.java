package com.example.PersonalAccounting.dto;

import com.example.PersonalAccounting.entity.enums.FinancialArrangementState;
import com.example.PersonalAccounting.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class FinancialArrangementDTO {

    private int id;

    @NotBlank
    @Length(min = 1, max = 50, message = "Name should contains less than 50 characters")
    private String name;

    @Min(value = 0, message = "Rate should be greater then 0")
    private int percent;

    @Min(value = 0, message = "Current sum should be greater then 0")
    private int startSum;

    @Min(value = 0, message = "Current sum should be greater then 0")
    private int currentSum;

    private int refundSum;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @Future
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

    private boolean fromToUserFunds;

    @NotNull
    private FinancialArrangementState state;

    private Status status;
}