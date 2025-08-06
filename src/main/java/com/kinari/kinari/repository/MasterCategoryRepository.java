package com.kinari.kinari.repository;

import com.kinari.kinari.entity.MasterCategoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterCategoryRepository extends JpaRepository<MasterCategoryTransaction, Long> {
}
