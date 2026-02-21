package com.example.MoneyManager.service;

import com.example.MoneyManager.dto.ExpenseDto;
import com.example.MoneyManager.model.Category;
import com.example.MoneyManager.model.Expense;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.CategoryRepo;
import com.example.MoneyManager.repository.ExpenseRepo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Builder
public class ExpenseService {

    private final CategoryRepo categoryRepo;
    private final ProfileService profileService;
    private final ExpenseRepo expenseRepo;

    public ExpenseDto addExpense(ExpenseDto expenseDto) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepo.findById(expenseDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Expense expense = toEntity(expenseDto, profile, category);
        Expense newExpense = expenseRepo.save(expense);
        return toDto(newExpense);

    }

    public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        List<Expense> expenses = expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return expenses.stream().map(this::toDto).toList();
    }

    public void deleteExpense(Long expenseId) {
        Profile profile = profileService.getCurrentProfile();
        Expense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if(expense.getProfile().getId().equals(profile.getId())) {
            expenseRepo.delete(expense);
        }
        else{
            throw new RuntimeException("Expense not found");
        }
    }

    public List<ExpenseDto> getLatest5ExpensesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        List<Expense> list = expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    public BigDecimal getTotalExpensesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepo.findTotalExpenseByProfileId(profile.getId());
        return total!=null?total:BigDecimal.ZERO;
    }

    private Expense toEntity(ExpenseDto expenseDto, Profile profile, Category category) {
        return Expense.builder()
                .name(expenseDto.getName())
                .icon(expenseDto.getIcon())
                .amount(expenseDto.getAmount())
                .date(expenseDto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDto toDto(Expense expense) {
        return ExpenseDto.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : null)
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdOn(expense.getCreatedOn())
                .updatedOn(expense.getUpdatedOn())
                .build();
    }

}
