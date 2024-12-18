package com.example.PersonalAccounting.dto;

import com.example.PersonalAccounting.entity.enums.TransactionCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO {

    private int id;

    @Min(value = 0, message = "Transaction sum can't be negative")
    private int sum;

    @Length(max = 150, message = "Comment should be less then 150 characters")
    private String comment;

    @NotNull(message = "Category can't be empty")
    private TransactionCategory category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateTime;

    @Column(name = "refill")
    private boolean refill;

    @Column(name = "periodic")
    private boolean periodic;
}
