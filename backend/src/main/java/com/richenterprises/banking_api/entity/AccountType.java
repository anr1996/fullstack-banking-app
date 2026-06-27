package com.richenterprises.banking_api.entity;

/**
 * Types of bank accounts.
 * They are stored as strings in the database to prevent corruption if enum values are
 * reordered.
 */
public enum AccountType {
    CHECKING,
    SAVINGS
    
}
