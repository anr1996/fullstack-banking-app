package com.richenterprises.banking_api.controller;

import com.richenterprises.banking_api.dto.TransferRequest;
import com.richenterprises.banking_api.entity.Transaction;
import com.richenterprises.banking_api.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 
 * The REST controller for atomic money transfers between accounts.
 */
@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    /**
     * The constructor injection of TransferService.
     */
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * This will Execute an atomic transfer between two accounts.
     * 
     * @param request (Requests the transfer details.)
     * @return (Returns a 201 created with both transaction records.)
     */
    @PostMapping
    public ResponseEntity<List<Transaction>> transfer(@Valid @RequestBody TransferRequest request) {
        List<Transaction> transactions = transferService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactions);
    }
}
