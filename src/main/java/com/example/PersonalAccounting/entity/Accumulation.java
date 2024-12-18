package com.example.PersonalAccounting.entity;

import com.example.PersonalAccounting.entity.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "accumulation")
public class Accumulation{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank
    @Length(min = 1, max = 50, message = "Name should contains less than 50 characters")
    @Column(name = "name")
    private String name;

    @Length(max = 150, message = "Comment should be less then 150 characters")
    @Column(name = "comment")
    private String comment;

    @Min(value = 0, message = "Current sum should be greater then 0")
    @Column(name = "current_sum")
    private int currentSum;

    @Min(value = 0, message = "Goal sum should be greater then 0")
    @Column(name = "goal_sum")
    private int goalSum;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private LocalDate endDate;

    @Nullable
    @Temporal(TemporalType.DATE)
    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
