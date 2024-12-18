package com.example.PersonalAccounting.services.entity_service_impl;

import com.example.PersonalAccounting.entity.Accumulation;
import com.example.PersonalAccounting.entity.User;
import com.example.PersonalAccounting.entity.Transaction;
import com.example.PersonalAccounting.entity.enums.Status;
import com.example.PersonalAccounting.entity.enums.TransactionCategory;
import com.example.PersonalAccounting.repositories.AccumulationRepository;
import com.example.PersonalAccounting.services.CrudService;
import com.example.PersonalAccounting.util.exceptions.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccumulationService implements CrudService<Accumulation> {

    private final AccumulationRepository accumulationRepository;
    private final TransactionService transactionService;

    @Autowired
    public AccumulationService(AccumulationRepository accumulationRepository,
                               TransactionService transactionService) {
        this.accumulationRepository = accumulationRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public Accumulation create(Accumulation accumulation) {
        User user = accumulation.getUser();
        user.addAccumulation(accumulation);

        accumulation.setStartDate(LocalDate.now());
        accumulation.setStatus(Status.ACTIVE);
        return accumulationRepository.save(accumulation);
    }

    @Transactional(readOnly = true)
    public List<Accumulation> getAll() {
        return accumulationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Accumulation> getAll(User user) {
        List<Accumulation> accumulations = accumulationRepository.findByUser(user);

        for(Accumulation accumulation :accumulations) {
            if(isOverdue(accumulation)) accumulation.setStatus(Status.OVERDUE);
        }
        return accumulations;
    }

    @Transactional(readOnly = true)
    public List<Accumulation> getByUserSinceDate(User user, LocalDate date) {
        return accumulationRepository.findByUserAndStartDateBetween(user, date, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Accumulation getOne(int id) {
        Accumulation accumulation = getOneNoCalculation(id);
        if(isOverdue(accumulation)) accumulation.setStatus(Status.OVERDUE);
        return accumulation;
    }

    private Accumulation getOneNoCalculation(int id) {
        return accumulationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No accumulation with such id: " + id));
    }

    @Transactional
    public Accumulation update(int id, Accumulation accumulation) {
        accumulationRepository.findById(id).ifPresentOrElse(a -> {
            a.setName(accumulation.getName());
            a.setCurrentSum(accumulation.getCurrentSum());
            a.setGoalSum(accumulation.getGoalSum());
            a.setComment(accumulation.getComment());
            a.setLastPaymentDate(accumulation.getLastPaymentDate());
            a.setEndDate(accumulation.getEndDate());
            if(accumulation.getStatus() != null)
                a.setStatus(accumulation.getStatus());
            else {
                if(a.getStatus() == null) a.setStatus(Status.ACTIVE);
            }
        }, () -> {
            throw new NoSuchElementException("No accumulation with id: " + id);
        });
        return accumulation;
    }

    @Transactional
    public void delete(int id) {
        accumulationRepository.deleteById(id);
    }

    @Transactional
    public Accumulation makePayment(int id, Transaction transaction) {
        Accumulation accumulation = getOne(id);
        if(accumulation.getStatus().equals(Status.EXECUTED))
            throw new PaymentException("Accumulation is executed");

        if(accumulation.getGoalSum() - accumulation.getCurrentSum() < transaction.getSum())
            transaction.setSum(accumulation.getGoalSum() - accumulation.getCurrentSum());

        if(transaction.getComment() == null)
            transaction.setComment(accumulation.getName() + ": accumulation payment");

        makePaymentCalculations(accumulation, transaction);
        transactionService.create(transaction);
        return update(id, accumulation);
    }

    @Transactional
    public Accumulation closeAccumulation(int id) {
        Accumulation accumulation = getOne(id);
        if (accumulation.getCurrentSum() == 0 && accumulation.getStatus().equals(Status.EXECUTED))
            throw new PaymentException("Accumulation is already closed");

        accumulation.setStatus(Status.EXECUTED);
        Transaction transaction = makeAccumulationCloseTransaction(accumulation.getCurrentSum(), accumulation.getUser());
        accumulation.setCurrentSum(0);

        transactionService.create(transaction);
        return update(id, accumulation);
    }

    private Transaction makeAccumulationCloseTransaction(int sum, User user) {
        Transaction transaction = new Transaction();
        transaction.setSum(sum);
        transaction.setUser(user);
        transaction.setRefill(true);
        transaction.setCategory(TransactionCategory.OTHER);
        transaction.setComment("Accumulation money");
        return transaction;
    }

    private void makePaymentCalculations(Accumulation accumulation, Transaction transaction) {
        accumulation.setStatus(Status.ACTIVE);
        accumulation.setLastPaymentDate(LocalDate.now());
        accumulation.setCurrentSum(accumulation.getCurrentSum() + transaction.getSum());
        if(accumulation.getCurrentSum() >= accumulation.getGoalSum())
            accumulation.setStatus(Status.EXECUTED);
    }

    private boolean isOverdue(Accumulation accumulation) {
        if(accumulation.getLastPaymentDate() == null) return false;

        return accumulation.getLastPaymentDate().isBefore(LocalDate.now());
    }
}
