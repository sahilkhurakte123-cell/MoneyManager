package com.example.MoneyManager.controller;

import com.example.MoneyManager.dto.ExpenseDto;
import com.example.MoneyManager.dto.IncomeDto;
import com.example.MoneyManager.service.ExpenseService;
import com.example.MoneyManager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping("/incomes")
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto) {
        IncomeDto incomeDto1 = incomeService.addIncome(incomeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeDto1);
    }

    @GetMapping("/incomes")
    public ResponseEntity<List<IncomeDto>> getAllIncomes() {
        List<IncomeDto> incomeDto = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(incomeDto);
    }

    @DeleteMapping("/incomes/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
