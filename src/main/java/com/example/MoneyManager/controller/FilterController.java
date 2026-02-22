package com.example.MoneyManager.controller;

import com.example.MoneyManager.dto.ExpenseDto;
import com.example.MoneyManager.dto.FilterDto;
import com.example.MoneyManager.dto.IncomeDto;
import com.example.MoneyManager.model.Income;
import com.example.MoneyManager.service.ExpenseService;
import com.example.MoneyManager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilterController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping("/filter")
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDto filterDto) {
        LocalDate startDate = filterDto.getStartDate() !=null ? filterDto.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDto.getEndDate()  !=null ? filterDto.getEndDate() : LocalDate.now();
        String  keyword = filterDto.getKeyword() !=null ? filterDto.getKeyword() : "";
        String sortField = filterDto.getSortField() !=null ? filterDto.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filterDto.getSortOrder())?Sort.Direction.DESC:Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        if("income".equals(filterDto.getType())){
            List<IncomeDto> list = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok().body(list);
        }else if("expense".equals(filterDto.getType())){
            List<ExpenseDto> list = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok().body(list);
        }
        else{
            return ResponseEntity.badRequest().body("income or expense type not found");
        }

    }

}
