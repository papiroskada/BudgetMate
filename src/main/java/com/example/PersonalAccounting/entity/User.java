package com.example.PersonalAccounting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Email not valid")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Password can't be empty")
    @Length(min = 8, max = 100, message = "Password should be between 8 and 100 characters")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "Name can't be empty")
    @Length(min = 1, max = 50, message = "Name should contains less than 50 characters")
    @Column(name = "name")
    private String name;

    @Column(name = "funds")
    private int funds;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "user")
    private List<Accumulation> accumulations;

    @OneToMany(mappedBy = "user")
    private List<FinancialArrangement> financialArrangements;


    public void addTransaction(Transaction transaction) {
        if(transactions == null) {
            transactions = List.of(transaction);
        }else {
            transactions.add(transaction);
        }
    }

    public void addAccumulation(Accumulation accumulation) {
        if(accumulations == null) {
            accumulations = List.of(accumulation);
        }else {
            accumulations.add(accumulation);
        }
    }

    public void addFinancialArrangement(FinancialArrangement financialArrangement) {
        if(financialArrangements == null) {
            financialArrangements = List.of(financialArrangement);
        }else {
            financialArrangements.add(financialArrangement);
        }
    }
}
