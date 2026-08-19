package com.hostflow.billing.service;

import com.hostflow.billing.dto.CreateExpenseRequest;
import com.hostflow.billing.entity.Expense;
import com.hostflow.billing.repository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public Expense record(UUID recordedByUserId, CreateExpenseRequest request) {
        Expense expense = new Expense(request.propertyId(), recordedByUserId, request.category(),
                request.description(), request.amount(), request.expenseDate());
        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Page<Expense> listByProperty(UUID propertyId, int limit, int offset) {
        return expenseRepository.findByPropertyId(propertyId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }
}
