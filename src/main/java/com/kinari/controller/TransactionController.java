package com.kinari.controller;

import com.kinari.dto.transaction.TransactionRequest;
import com.kinari.dto.transaction.TransactionResponse;
import com.kinari.entity.Transaction;
import com.kinari.entity.User;
import com.kinari.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody TransactionRequest request, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionService.create(request, user);
        return ResponseEntity.ok(Map.of("message", "Transaction created successfully"));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(@AuthenticationPrincipal User user) {
        List<Transaction> transaction = transactionService.findByUser(user);
        List<TransactionResponse> responses = transaction.stream().map(transactionService::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Long id, @RequestBody TransactionRequest request, @AuthenticationPrincipal User user) {
        boolean updated = transactionService.update(id, request, user);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Update successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Update Fail"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            transactionService.delete(id, user);
            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Not found"));
        }
    }

}
