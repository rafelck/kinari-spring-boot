package com.kinari.kinari.controller;

import com.kinari.kinari.dto.account.AccountRequest;
import com.kinari.kinari.dto.account.AccountResponse;
import com.kinari.kinari.entity.Account;
import com.kinari.kinari.entity.User;
import com.kinari.kinari.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody AccountRequest request, @AuthenticationPrincipal User user) {
        accountService.create(request, user);
        return ResponseEntity.ok(Map.of("message", "Account created successfully"));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAll(@AuthenticationPrincipal User user) {
        List<Account> accounts = accountService.findByUser(user);
        List<AccountResponse> responses = accounts.stream().map(accountService::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Long id, @RequestBody AccountRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        boolean updated = accountService.update(id, request, user);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Update successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Update Fail"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            accountService.softDelete(id, user);
            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Not found"));
        }
    }

}
