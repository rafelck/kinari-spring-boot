package com.kinari.repository;

import com.kinari.entity.Transaction;
import com.kinari.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);
    Optional<Transaction> findByIdAndUser(Long id, User user);
}
