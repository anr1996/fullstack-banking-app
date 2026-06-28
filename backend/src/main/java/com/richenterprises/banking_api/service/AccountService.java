package com.richenterprises.banking_api.service;

import com.richenterprises.banking_api.entity.Account;
import com.richenterprises.banking_api.entity.AccountType;
import com.richenterprises.banking_api.entity.User;
import com.richenterprises.banking_api.repository.AccountRepository;
import com.richenterprises.banking_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * The service for account operations.
 * This will handle creation and retrieval of bank accounts.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
            }
        
    /**
     * This will create a new account for the currently authenticated user. 
     * 
     * @param type (The account type (CHECKING or SAVINGS).)
     * @return (Returns the created account.)
     */
    public Account createAccount(AccountType type) {
        // This gets the current user's email from the JWT token. 
        String email = getCurrentUserEmail();

        // This will find the user in the database.
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found."));
        
        // This will generate a unique account number.
        String accountNumber = generateAccountNumber();

        // This will build and save the account.
        Account account = Account.builder()
                .user(user) 
                .accountNumber(accountNumber)
                .type(type)
                .build();

        return accountRepository.save(account);
    }

    /**
     * This will list all accounts belonging to the currently authenticated user. 
     * 
     * @return (Returns a list of accounts.)
     */
    @Transactional(readOnly = true)
    public List<Account> getCurrentUserAccounts() {
        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
             .orElseThrow(() -> new RuntimeException("User not found."));
        
             return accountRepository.findByUserId(user.getId());
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext()
               .getAuthentication()
               .getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
            }
        
   /** 
    * This will generate a unique account number. 
    * Format: ACC- followed by a UUID segment. 
    */
   private String generateAccountNumber() {
    return "ACC-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
   }
    
}
