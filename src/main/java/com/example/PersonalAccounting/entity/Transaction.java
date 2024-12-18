package com.example.PersonalAccounting.entity;

import com.example.PersonalAccounting.entity.enums.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Min(value = 0, message = "Transaction sum can't be negative")
    @Column(name = "sum")
    private int sum;

    @Length(max = 150, message = "Comment should be less then 150 characters")
    @Column(name = "comment")
    private String comment;

    @NotNull(message = "Category can't be empty")
    @Enumerated(value = EnumType.STRING)
    @Column(name = "category")
    private TransactionCategory category;

    @Column(name = "refill")
    private boolean refill;

    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "periodic")
    private boolean periodic;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public boolean isEmpty() {
        return sum == 0 || category == null;
    }

    @Override
    public Transaction clone() {

        try {
            Transaction clone = (Transaction) super.clone();
            clone.setSum(sum);
            clone.setDateTime(dateTime);
            clone.setCategory(category);
            clone.setRefill(refill);
            clone.setComment(comment);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;

        if (id != that.id) return false;
        if (sum != that.sum) return false;
        if (refill != that.refill) return false;
        if (periodic != that.periodic) return false;
        if (!Objects.equals(comment, that.comment)) return false;
        if (category != that.category) return false;
        if (!Objects.equals(dateTime, that.dateTime)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + sum;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + category.hashCode();
        result = 31 * result + (refill ? 1 : 0);
        result = 31 * result + (dateTime != null ? dateTime.hashCode() : 0);
        result = 31 * result + (periodic ? 1 : 0);
        result = 31 * result + user.hashCode();
        return result;
    }
}
