package com.kinari.kinari.controller;

import com.kinari.kinari.dto.category_transaction.CategoryTransactionRequest;
import com.kinari.kinari.dto.category_transaction.CategoryTransactionResponse;
import com.kinari.kinari.entity.CategoryTransaction;
import com.kinari.kinari.entity.User;
import com.kinari.kinari.service.CategoryTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories-transactions")
public class CategoryTransactionController {
    private final CategoryTransactionService categoryTransactionService;

    public CategoryTransactionController(CategoryTransactionService categoryTransactionService) {
        this.categoryTransactionService = categoryTransactionService;
    }

    @PostMapping
    public ResponseEntity<CategoryTransaction> create(@RequestBody CategoryTransactionRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryTransactionService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<CategoryTransactionResponse>> getAll(@AuthenticationPrincipal User user) {
        List<CategoryTransaction> categoryTransactions = categoryTransactionService.findByUser(user);
        List<CategoryTransactionResponse> responses = categoryTransactions.stream()
                .map(categoryTransactionService::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryTransactionRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        boolean updated = categoryTransactionService.update(id, request, user);
        if (updated) {
            return ResponseEntity.ok("Category updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found or not owned by user");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            categoryTransactionService.softDelete(id, user);
            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Not found"));
        }
    }

}
