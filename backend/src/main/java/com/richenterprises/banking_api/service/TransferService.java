package com.richenterprises.banking_api.service;

import com.richenterprises.banking_api.dto.TransferRequest;
import com.richenterprises.banking_api.entity.Account;
import com.richenterprises.banking_api.entity.Transaction;
import com.richenterprises.banking_api.entity.TransactionType;
import com.richenterprises.banking_api.repository.AccountRepository;
import com.richenterprises.banking_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The service for atomic money transfers between accounts.
 * This will ensure both debit and credit succeed, or neither does.
 */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * The constructor injection of repositories
     */
    public TransferService(AccountRepository accountRepository, 
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * This will Transfer money from one account to another atomically.
     * 
     * @param request (Requests the transfer details.)
     * @return (Returns a list of created transactions (debit and credit).)
     */
    @Transactional
    public List<Transaction> transfer(TransferRequest request) {
        Long fromId = request.getFromAccountId();
        Long toId = request.getToAccountId();
        Long amount = request.getAmount();

        // Validate accounts are different.
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("You cannot transfer to the same account.");
        }

        // Validate amount is positive.
        if (amount <= 0) {
            throw new IllegalArgumentException("The transfer amount must be positive.");
        }

        // This will load both accounts.
        // It will throw an exception if either does not exist.
        Account fromAccount = accountRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("The source account was not found"));
        Account toAccount = accountRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("The destination account was not found"));

        // Validate sufficient funds.
        if(fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Perform the transfer.
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create the transaction records for the audit trail.
        Transaction debitTransaction = Transaction.builder()
                    .account(fromAccount)
                    .amount(amount)
                    .type(TransactionType.WITHDRAWAL)
                    .description("Transfer to account " + toId + ": " + request.getDescription())
                    .build();
        
        Transaction creditTransaction = Transaction.builder()
                    .account(toAccount)
                    .amount(amount)
                    .type(TransactionType.DEPOSIT)
                    .description("Transfer from account " + fromId + ": " + request.getDescription())
                    .build();
        
        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        return List.of(debitTransaction, creditTransaction);
    }
}
