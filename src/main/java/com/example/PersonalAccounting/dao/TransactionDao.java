package com.example.PersonalAccounting.dao;

import com.example.PersonalAccounting.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class TransactionDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchCreate(List<Transaction> transactions) {
        jdbcTemplate.batchUpdate("INSERT INTO personal_accounting.transaction(sum, comment, refill, date_time," +
                        " user_id, category, periodic) VALUES (?,?,?,?,?,?,?)",

                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Transaction transaction = transactions.get(i);

                        ps.setInt(1, transaction.getSum());
                        ps.setString(2, transaction.getComment());
                        ps.setBoolean(3, transaction.isRefill());
                        ps.setString(4, transaction.getDateTime().toString());
                        ps.setInt(5, transaction.getUser().getId());
                        ps.setString(6, transaction.getCategory().toString());
                        ps.setBoolean(7, transaction.isPeriodic());
                    }

                    @Override
                    public int getBatchSize() {
                        return transactions.size();
                    }
                });
    }
}
