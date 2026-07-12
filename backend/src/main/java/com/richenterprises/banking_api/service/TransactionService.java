package com.richenterprises.banking_api.service;

import com.richenterprises.banking_api.entity.Account;
import com.richenterprises.banking_api.entity.Transaction;
import com.richenterprises.banking_api.entity.TransactionType;
import com.richenterprises.banking_api.repository.AccountRepository;
import com.richenterprises.banking_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The service for transaction operations.
 * This will handle deposits, withdrawals, and transaction history retrieval.
 */
@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /**
     * The constructor injection of repositories.
     */
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * This will deposit money into an account.
     * 
     * @param accountId (The target account ID.)
     * @param amount (The amount in cents (must be positive).)
     * @param description (an optional memo.)
     * @return (Returns the created transaction.)
     */
    @Transactional
    public Transaction deposit(Long accountId, Long amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The deposit amount must be positive.");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("The account was not found."));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .type(TransactionType.DEPOSIT)
            .description(description)
            .build();

        return transactionRepository.save(transaction);
    }

    /**
     * This will withdraw money from an account.
     * 
     * @param accountId (the source account ID.)
     * @param amount (The amount in cents (must positive).)
     * @param description (an optional memo.)
     * @return (Returns the created transaction.)
     */
    @Transactional
    public Transaction withdraw(Long accountId, Long amount, String description) {
        if (amount <=0) {
            throw new IllegalArgumentException("The withdrawal amount must be positive.");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("The account was not found"));

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .type(TransactionType.WITHDRAWAL)
            .description(description)
            .build();

        return transactionRepository.save(transaction);
    }

    /**
     * This will return transaction history for a specific account.
     * 
     * @param accountId (The account ID.)
     * @return (Returns a list of transactions.)
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAccountTransactions(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }


}