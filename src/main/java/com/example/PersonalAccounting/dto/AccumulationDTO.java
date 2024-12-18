package com.example.PersonalAccounting.dto;

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
@Setter
@Getter
public class AccumulationDTO{

    private int id;

    @NotBlank
    @Length(min = 1, max = 50, message = "Name should contains less than 50 characters")
    private String name;

    @Length(max = 150, message = "Comment should be less then 150 characters")
    private String comment;

    @Min(value = 0, message = "Current sum should be greater then 0")
    private int currentSum;

    @Min(value = 0, message = "Goal sum should be greater then 0")
    private int goalSum;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate startDate;

    @Future
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate lastPaymentDate;

    private Status status;
}
