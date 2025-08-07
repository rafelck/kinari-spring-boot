package com.kinari.service;

import com.kinari.dto.transaction.TransactionRequest;
import com.kinari.dto.transaction.TransactionResponse;
import com.kinari.entity.Account;
import com.kinari.entity.CategoryTransaction;
import com.kinari.entity.Transaction;
import com.kinari.entity.User;
import com.kinari.repository.AccountRepository;
import com.kinari.repository.CategoryTransactionRepository;
import com.kinari.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryTransactionRepository categoryTransactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryTransactionRepository categoryTransactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryTransactionRepository = categoryTransactionRepository;
        this.accountRepository = accountRepository;
    }

    public Transaction create(TransactionRequest request, User user) {
        Account account = accountRepository.findByIdAndUserAndDeletedFalse(request.getAccountId(), user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        CategoryTransaction categoryTransaction = categoryTransactionRepository.findByIdAndUserAndDeletedFalse(request.getCategoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        String type = categoryTransaction.getType();


        if (type.equalsIgnoreCase("expense") && account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setCategoryTransaction(categoryTransaction);
        transaction.setDescription(request.getDescription());
        transaction.setAccount(account);
        transaction.setUser(user);
        transaction.setDate(LocalDateTime.now());

        transactionRepository.save(transaction);

        // Update balance akun
        if (type.equalsIgnoreCase("income")) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        }
        accountRepository.save(account);

        return transaction;
    }

    public List<Transaction> findByUser(User user) {
        return transactionRepository.findAllByUser(user);
    }

    public TransactionResponse toResponse(Transaction transaction) {
        TransactionResponse dto = new TransactionResponse();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setType(transaction.getCategoryTransaction().getType());
        dto.setCategory(transaction.getCategoryTransaction().getName());
        dto.setAccount(transaction.getAccount().getName());
        dto.setDate(transaction.getDate());

        return dto;
    }

    public boolean update(Long id, TransactionRequest request, User user) {
        Optional<Transaction> optionalTransaction = transactionRepository.findByIdAndUser(id, user);

        Account account = accountRepository.findByIdAndUserAndDeletedFalse(request.getAccountId(), user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        CategoryTransaction categoryTransaction = categoryTransactionRepository.findByIdAndUserAndDeletedFalse(request.getCategoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (optionalTransaction.isEmpty()) {
            return false;
        }

        Transaction transaction = optionalTransaction.get();
        BigInteger oldAmount = transaction.getAmount();
        if (request.getAmount() != null) transaction.setAmount(request.getAmount());
        if (request.getDescription() != null) transaction.setDescription(request.getDescription());
        if (request.getAccountId() != null) transaction.setAccount(account);
        if (request.getCategoryId() != null) transaction.setCategoryTransaction(categoryTransaction);

        transactionRepository.save(transaction);

        // Update balance akun
        String type = categoryTransaction.getType();
        if (type.equalsIgnoreCase("income")) {
            account.setBalance(account.getBalance().add(request.getAmount()));
            account.setBalance(account.getBalance().subtract(oldAmount));
        } else {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
            account.setBalance(account.getBalance().add(oldAmount));
        }
        accountRepository.save(account);

        return true;
    }

    public boolean delete(Long id,User user) {
        Optional<Transaction> optionalTransaction = transactionRepository.findByIdAndUser(id, user);

        if (optionalTransaction.isEmpty()) {
            return false;
        }

        Transaction transaction = optionalTransaction.get();
        Account account = transaction.getAccount();

        // Perbarui balance sesuai tipe transaksi
        if (transaction.getCategoryTransaction().getType().equalsIgnoreCase("income")) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else if (transaction.getCategoryTransaction().getType().equalsIgnoreCase("expense")) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }

        accountRepository.save(account); // simpan perubahan balance

        transactionRepository.deleteById(id); // hard delete

        return true;
    }
}
