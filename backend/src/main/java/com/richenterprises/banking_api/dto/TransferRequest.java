package com.richenterprises.banking_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * This will request a payload for transferring money between accounts.
 */
public class TransferRequest {

    /**
     * The source account ID. The funds are withdrawn from this account.
     */
    @NotNull
    private Long fromAccountId;

    /**
     * The destination account ID. The funds are deposited into this account.
     */
    @NotNull
    private Long toAccountId;

    /**
     * The transfer amount in integer cents. It must be positive.
     */
    @NotNull
    @Min(1)
    private Long amount;

    /**
     * Optional description or memo for the transfer;
     */
    private String description;

    /**
     * This will return the source account ID.
     * 
     * @return (Returns the account to withdraw from.)
     */
    public Long getFromAccountId() {
        return fromAccountId;
    }


    /**
     * This will set the source account ID.
     * 
     * @param fromAccountId (The account to withdraw from.)
     */
    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    /**
     * This will return the destination account ID.
     * 
     * @return (Returns the account to deposit into.)
     */
    public Long getToAccountId() {
        return toAccountId;
    }

    /**
     * This will set the destination account ID.
     * 
     * @param toAccountId (The account to deposit into.)
     */
    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    /**
     * This will return the transfer amount in cents.
     * 
     * @return (Returns the amount to transfer.)
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * This will set the transfer amount in cents.
     * 
     * @param amount (The amount to transfer.)
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    
    /**
     * This will return the optional transfer description.
     * 
     * @return (Return the description, or null of it is not set.)
     */
    public String getDescription() {
        return description;
    }

    /**
     * This will set the optional transfer description.
     * 
     * @param description (The description to attach to the transfer.)
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
