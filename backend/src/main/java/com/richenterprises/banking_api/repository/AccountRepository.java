package com.richenterprises.banking_api.repository;

import com.richenterprises.banking_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The Repository for Account entities.
 * This Provides the CRUD operations and custom queries for accounts.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * This finds all accounts which belong to a specific user.
     * It is used by customers to view their own accounts.
     * 
     * @param userId (The user ID of the user.)
     * @return (returns the list of accounts owned by the user.)
     */
    List<Account> findByUserId(Long userId);
}
