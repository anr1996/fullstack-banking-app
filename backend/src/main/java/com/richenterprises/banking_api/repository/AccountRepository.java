package com.richenterprises.banking_api.repository;

import com.richenterprises.banking_api.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * This will find an account by ID with a pessimistic write Lock.
     * The Lock is held until the transaction commits or rolls back, which prevents concurrent 
     * modifications during a transfer.
     * 
     * @param id (The account ID.)
     * @return (Returns the Locked account, or empty if not found.)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = ?1")
    Optional<Account> findByIdWithLock(Long id);
}
