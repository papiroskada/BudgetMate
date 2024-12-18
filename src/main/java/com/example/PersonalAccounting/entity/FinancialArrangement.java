package com.example.PersonalAccounting.entity;

import com.example.PersonalAccounting.entity.enums.FinancialArrangementState;
import com.example.PersonalAccounting.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "financial_arrangement")
public class FinancialArrangement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank
    @Length(min = 1, max = 50, message = "Name should contains less than 50 characters")
    @Column(name = "name")
    private String name;

    @Min(value = 0, message = "Rate should be greater then 0")
    @Column(name = "percent")
    private int percent;

    @Min(value = 0, message = "Current sum should be greater then 0")
    @Column(name = "start_sum")
    private int startSum;

    @Min(value = 0, message = "Current sum should be greater then 0")
    @Column(name = "current_sum")
    private int currentSum;

    @Transient
    private int refundSum;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private LocalDate startDate;

    //Add only in dto @Future
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "from_to_user_funds")
    private boolean fromToUserFunds;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "state")
    private FinancialArrangementState state;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
