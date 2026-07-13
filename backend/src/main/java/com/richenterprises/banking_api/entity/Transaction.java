package com.richenterprises.banking_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The financial transaction entity.
 * This will map to the 'transactions' table in PostgreSQL;
 * Each transaction is linked to one account and records a deposit or withdrawal.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    /**
     * The primary key. This uses PostgreSQL's BIGSERIAL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The account this transaction belongs to.
     * Many transactions can belong to one account.
     * LAZY fetch means account data is loaded only when it is accessed.
     */
    @JsonIgnore
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    /**
     * The transaction amount in integer cents.
     * It is always positive. The type (DEPOSIT/WITHDRAWAL) determines the direction.
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * DEPOSIT or WITHDRAWAL. 
     * It is stored as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * This is an optional description or memo for the transaction.
     */
    @Column(length = 255)
    private String description;

    /**
     * The timestamp is set automatically on creation.
     * It is immutable after insert.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
