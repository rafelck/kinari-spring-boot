package com.kinari.kinari.service;

import com.kinari.kinari.dto.account.AccountRequest;
import com.kinari.kinari.dto.account.AccountResponse;
import com.kinari.kinari.entity.Account;
import com.kinari.kinari.entity.User;
import com.kinari.kinari.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    final private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account create(AccountRequest request, User user) {
        Account account = new Account();
        account.setName(request.getName());
        account.setBalance(request.getBalance() != null ? request.getBalance() : 0.0);
        account.setUser(user);
        return accountRepository.save(account);
    }

    public List<Account> findByUser(User user) {
        return accountRepository.findByUserAndDeletedFalse(user);
    }

    public AccountResponse toResponse(Account account) {
        AccountResponse dto = new AccountResponse();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setBalance(account.getBalance());
        dto.setUserId(account.getUser().getId());

        return dto;
    }

    public boolean update(Long id, AccountRequest request, User user) {
        Optional<Account> optionalAccount = accountRepository.findByIdAndUserAndDeletedFalse(id, user);

        if (optionalAccount.isEmpty()) {
            return false;
        }

        Account account = optionalAccount.get();
        if (request.getName() != null) account.setName(request.getName());

        accountRepository.save(account);
        return true;
    }

    public void softDelete(Long accountId, User user) {
        Account account = accountRepository.findByIdAndUserAndDeletedFalse(accountId, user)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        account.setDeleted(true);
        accountRepository.save(account);
    }

}
