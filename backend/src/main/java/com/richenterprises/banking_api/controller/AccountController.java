package com.richenterprises.banking_api.controller;

import com.richenterprises.banking_api.entity.Account;
import com.richenterprises.banking_api.entity.AccountType;
import com.richenterprises.banking_api.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The REST controller for account operations.
 * Authenticated users can create and view their own accounts.
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * This will create a new account for the currently authenticated user. 
     * 
     * @param type (The account type (CHECKING or SAVINGS).)
     * @return (Returns 201 created with the new account.)
     */
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestParam AccountType type) {
        Account account = accountService.createAccount(type);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    /**
     * This will list all accounts belonging to the currently authenticated user. 
     * 
     * @return 200 OK (Returns 200 OK with the list of accounts.)
     */
    @GetMapping
    public ResponseEntity<List<Account>> getMyAccounts() {
        List<Account> accounts = accountService.getCurrentUserAccounts();
        return ResponseEntity.ok(accounts);
    }
}

// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZSI6IkNVU1RPTUVSIiwiaWF0IjoxNzgyNjIwODA4LCJleHAiOjE3ODI2MjE3MDh9.yWLaQ_n2L-4bBACisOYFm86akOgAoTT_aQ27gyaD0Wc