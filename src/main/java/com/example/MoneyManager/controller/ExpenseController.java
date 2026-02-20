package com.example.MoneyManager.controller;

import com.example.MoneyManager.dto.ExpenseDto;
import com.example.MoneyManager.model.Expense;
import com.example.MoneyManager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto expenseDto) {
        ExpenseDto expenseDto1 = expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseDto1);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseDto>> getAllExpenses() {
        List<ExpenseDto> expenseDto = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(expenseDto);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
