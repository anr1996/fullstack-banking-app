package com.richenterprises.banking_api.controller;

import com.richenterprises.banking_api.entity.Account;
import com.richenterprises.banking_api.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin-only REST endpoints used for system-wide operations;
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AccountService accountService;

    /**
     * The constructor injection of AccountService.
     */
    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * This will return all accounts in the system. 
     * Restricted to ADMIN role.
     * 
     * @return (Returns 200 OK with a list of all the accounts.)
     */
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }


    
    
}
