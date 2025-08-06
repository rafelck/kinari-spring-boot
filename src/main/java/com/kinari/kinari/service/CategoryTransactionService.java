package com.kinari.kinari.service;

import com.kinari.kinari.dto.category_transaction.CategoryTransactionRequest;
import com.kinari.kinari.dto.category_transaction.CategoryTransactionResponse;
import com.kinari.kinari.entity.CategoryTransaction;
import com.kinari.kinari.entity.User;
import com.kinari.kinari.repository.CategoryTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryTransactionService {
    private final CategoryTransactionRepository categoryTransactionRepository;

    public CategoryTransactionService(CategoryTransactionRepository categoryTransactionRepository) {
        this.categoryTransactionRepository = categoryTransactionRepository;
    }

    public CategoryTransaction create(CategoryTransactionRequest request, User user) {
        CategoryTransaction categoryTransaction = new CategoryTransaction();
        categoryTransaction.setName(request.getName());
        categoryTransaction.setType(request.getType());
        categoryTransaction.setIcon(request.getIcon());
        categoryTransaction.setUser(user);
        return categoryTransactionRepository.save(categoryTransaction);
    }

    public List<CategoryTransaction> findByUser(User user) {
        return categoryTransactionRepository.findByUserAndDeletedFalse(user);
    }

    public CategoryTransactionResponse toResponse(CategoryTransaction category) {
        CategoryTransactionResponse dto = new CategoryTransactionResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType());
        dto.setIcon(category.getIcon());
        dto.setUserId(category.getUser().getId());

        return dto;
    }

    public boolean update(Long id, CategoryTransactionRequest request, User user) {
        Optional<CategoryTransaction> optionalCategoryTransaction = categoryTransactionRepository.findByIdAndUserAndDeletedFalse(id, user);

        if (optionalCategoryTransaction.isEmpty()) {
            return false;
        }

        CategoryTransaction categoryTransaction = optionalCategoryTransaction.get();
        if (request.getName() != null) categoryTransaction.setName(request.getName());
        if (request.getType() != null) categoryTransaction.setType(request.getType());
        if (request.getIcon() != null) categoryTransaction.setIcon(request.getIcon());

        categoryTransactionRepository.save(categoryTransaction);
        return true;
    }

    public void softDelete(Long categoryId, User user) {
        CategoryTransaction categoryTransaction = categoryTransactionRepository.findByIdAndUserAndDeletedFalse(categoryId, user)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        categoryTransaction.setDeleted(true);
        categoryTransactionRepository.save(categoryTransaction);
    }
}
