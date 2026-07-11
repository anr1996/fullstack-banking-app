package com.richenterprises.banking_api.repository;

import com.richenterprises.banking_api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The repository for Transaction entities.
 * This will provide CRUD operations and custom queries for transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * This will find all transactions for a specific account.
     * It is used to display transaction history to the account owner.
     * 
     * @param accountId (The ID of the account.)
     * @return (returns the list of transactions ordered by the creation time (default).)
     */
    List<Transaction> findByAccountId(Long accountId);
    
}
