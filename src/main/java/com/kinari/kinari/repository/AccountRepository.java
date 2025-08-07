package com.kinari.kinari.repository;

import com.kinari.kinari.entity.Account;
import com.kinari.kinari.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserAndDeletedFalse(User user);
    Optional<Account> findByIdAndUserAndDeletedFalse(Long id, User user);
}
