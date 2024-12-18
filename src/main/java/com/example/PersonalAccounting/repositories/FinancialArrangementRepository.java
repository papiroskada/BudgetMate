package com.example.PersonalAccounting.repositories;

import com.example.PersonalAccounting.entity.FinancialArrangement;
import com.example.PersonalAccounting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FinancialArrangementRepository extends JpaRepository<FinancialArrangement, Integer> {

    @Query("SELECT fa FROM FinancialArrangement fa LEFT join FETCH fa.user")
    List<FinancialArrangement> findAll();

    List<FinancialArrangement> findByUserAndStartDateBetween(User user, LocalDate start, LocalDate end);

    List<FinancialArrangement> findByUser(User user);
}
