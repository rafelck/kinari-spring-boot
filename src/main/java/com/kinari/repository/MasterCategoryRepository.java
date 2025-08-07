package com.kinari.repository;

import com.kinari.entity.MasterCategoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterCategoryRepository extends JpaRepository<MasterCategoryTransaction, Long> {
}
