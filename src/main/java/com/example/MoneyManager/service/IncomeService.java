package com.example.MoneyManager.service;

import com.example.MoneyManager.dto.ExpenseDto;
import com.example.MoneyManager.dto.IncomeDto;
import com.example.MoneyManager.model.Category;
import com.example.MoneyManager.model.Expense;
import com.example.MoneyManager.model.Income;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.CategoryRepo;
import com.example.MoneyManager.repository.ExpenseRepo;
import com.example.MoneyManager.repository.IncomeRepo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Builder
public class IncomeService {

    private final CategoryRepo categoryRepo;
    private final ProfileService profileService;
    private final IncomeRepo incomeRepo;

    public IncomeDto addIncome(IncomeDto incomeDto) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepo.findById(incomeDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Income income = toEntity(incomeDto, profile, category);
        Income newIncome = incomeRepo.save(income);
        return toDto(newIncome);

    }

    public List<IncomeDto> getCurrentMonthIncomesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        List<Income> incomes = incomeRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return incomes.stream().map(this::toDto).toList();
    }

    public void deleteIncome(Long incomeId) {
        Profile profile = profileService.getCurrentProfile();
        Income income = incomeRepo.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if(income.getProfile().getId().equals(profile.getId())) {
            incomeRepo.delete(income);
        }
        else{
            throw new RuntimeException("Income not found");
        }
    }

    public List<IncomeDto> getLatest5IncomesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        List<Income> list = incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    public BigDecimal getTotalIncomesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepo.findTotalIncomeByProfileId(profile.getId());
        return total!=null?total:BigDecimal.ZERO;
    }

    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        Profile profile = profileService.getCurrentProfile();
        List<Income> list = incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
        return list.stream().map(this::toDto).toList();
    }

    private Income toEntity(IncomeDto incomeDto, Profile profile, Category category) {
        return Income.builder()
                .name(incomeDto.getName())
                .icon(incomeDto.getIcon())
                .amount(incomeDto.getAmount())
                .date(incomeDto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDto toDto(Income income) {
        return IncomeDto.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : null)
                .amount(income.getAmount())
                .date(income.getDate())
                .createdOn(income.getCreatedOn())
                .updatedOn(income.getUpdatedOn())
                .build();
    }

}
