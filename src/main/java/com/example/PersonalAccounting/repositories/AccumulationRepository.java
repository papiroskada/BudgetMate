package com.example.PersonalAccounting.repositories;

import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AccumulationRepository extends JpaRepository<Accumulation, Integer> {

    @Query("SELECT a FROM Accumulation a LEFT join FETCH a.user")
    List<Accumulation> findAll();

    List<Accumulation> findByUserAndStartDateBetween(User user, LocalDate start, LocalDate end);

    List<Accumulation> findByUser(User user);
}
