package com.hostflow.billing.controller;

import com.hostflow.billing.dto.BudgetVarianceResponse;
import com.hostflow.billing.dto.SetBudgetRequest;
import com.hostflow.billing.entity.Budget;
import com.hostflow.billing.service.BudgetService;
import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/budgets")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UUID>> setBudget(@Valid @RequestBody SetBudgetRequest request) {
        Budget budget = budgetService.setBudget(request);
        return ResponseEntity.ok(ApiResponse.success(budget.getId()));
    }

    @GetMapping("/variance")
    public ResponseEntity<ApiResponse<List<BudgetVarianceResponse>>> variance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        return ResponseEntity.ok(ApiResponse.success(budgetService.varianceForMonth(month)));
    }
}
