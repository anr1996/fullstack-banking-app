package com.richenterprises.banking_api.controller;

import com.richenterprises.banking_api.entity.Transaction;
import com.richenterprises.banking_api.service.AccountService;
import com.richenterprises.banking_api.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The REST controller for transaction operations.
 * This will allow authenticated users to deposit, withdraw, and view history for their own
 * accounts.
 */
@RestController
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    /**
     * The constructor injection of services.
     */
    public TransactionController(TransactionService transactionService, 
                                 AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    /**
     * This will deposit money into the specified account.
     * 
     * @param accountId (The target account ID.)
     * @param amount (The amount in cents.)
     * @param description (An optional memo.)
     * @return (A 201 created wiht the transaction record.)
     */
    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@PathVariable Long accountId,
                                               @RequestParam Long amount,
                                               @RequestParam(required = false) String description) {
        Transaction transaction = transactionService.deposit(accountId, amount, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    /**
     * This will withdraw money from the specified account.
     * 
     * @param accountId (The source account ID.)
     * @param amount (The amount in cents.)
     * @param description (An optional memo.)
     * @return (Returns a 201 created with the transaction record.)
     */
    @PostMapping("/withdrawal")
    public ResponseEntity<Transaction> withdraw(@PathVariable Long accountId,
                                                @RequestParam Long amount,
                                                @RequestParam(required = false) 
                                                String description) {
        Transaction transaction = transactionService.withdraw(accountId, amount, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);                                            
    }

    /**
     * This will return the transaction history for the specified account.
     * 
     * @param accountId (The account ID.)
     * @return (returns 200 OK with a list of transactions.)
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }                                          
}
