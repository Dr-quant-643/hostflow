package com.hostflow.billing.controller;

import com.hostflow.billing.dto.CreateExpenseRequest;
import com.hostflow.billing.dto.ExpenseResponse;
import com.hostflow.billing.entity.Expense;
import com.hostflow.billing.service.ExpenseService;
import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/expenses")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(
            @Valid @RequestBody CreateExpenseRequest request, @AuthenticationPrincipal Jwt jwt) {
        Expense expense = expenseService.record(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ExpenseResponse.from(expense)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> listByProperty(
            @RequestParam UUID propertyId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Page<ExpenseResponse> page = expenseService.listByProperty(propertyId, limit, offset).map(ExpenseResponse::from);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
