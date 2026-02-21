package com.example.MoneyManager.service;

import com.example.MoneyManager.dto.ExpenseDto;
import com.example.MoneyManager.dto.IncomeDto;
import com.example.MoneyManager.dto.RecentTransactionsDto;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProfileService profileService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    public Map<String, Object> getDashboardData() {
        Profile profile = profileService.getCurrentProfile();
        Map<String, Object> data = new LinkedHashMap<>();
        List<IncomeDto> incomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDto> expenses = expenseService.getLatest5ExpensesForCurrentUser();

        List<RecentTransactionsDto> recentTransactionsDto =
                Stream.concat(
                                incomes.stream().map(income ->
                                        RecentTransactionsDto.builder()
                                                .id(income.getId())
                                                .profileId(profile.getId())
                                                .icon(income.getIcon())
                                                .name(income.getName())
                                                .amount(income.getAmount())
                                                .date(income.getDate())
                                                .createdOn(income.getCreatedOn())
                                                .updatedOn(income.getUpdatedOn())
                                                .type("income")
                                                .build()
                                ),
                                expenses.stream().map(expense ->
                                        RecentTransactionsDto.builder()
                                                .id(expense.getId())
                                                .profileId(profile.getId())
                                                .icon(expense.getIcon())
                                                .name(expense.getName())
                                                .amount(expense.getAmount())
                                                .date(expense.getDate())
                                                .createdOn(expense.getCreatedOn())
                                                .updatedOn(expense.getUpdatedOn())
                                                .type("expense")
                                                .build()
                                )
                        )
                        .sorted((a, b) -> {
                            int cmp = b.getDate().compareTo(a.getDate());
                            if (cmp == 0 && a.getCreatedOn() != null && b.getCreatedOn() != null) {
                                cmp = b.getCreatedOn().compareTo(a.getCreatedOn());
                            }
                            return cmp;
                        })
                        .toList();

        data.put("TotalBalance", incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        data.put("TotalIncome", incomeService.getTotalIncomesForCurrentUser());
        data.put("TotalExpenses", expenseService.getTotalExpensesForCurrentUser());
        data.put("Recent5Expenses", expenses);
        data.put("Recent5Incomes", incomes);
        data.put("RecentTransactions", recentTransactionsDto);
        return data;
    }

}
