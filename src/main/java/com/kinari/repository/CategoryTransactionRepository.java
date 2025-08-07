package com.kinari.repository;

import com.kinari.entity.CategoryTransaction;
import com.kinari.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryTransactionRepository extends JpaRepository<CategoryTransaction, Long> {
    List<CategoryTransaction> findByUserAndDeletedFalse(User user);
    Optional<CategoryTransaction> findByIdAndUserAndDeletedFalse(Long id, User user);

}
