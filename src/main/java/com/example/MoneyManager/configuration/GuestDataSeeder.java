package com.example.MoneyManager.configuration;

import com.example.MoneyManager.model.Category;
import com.example.MoneyManager.model.Expense;
import com.example.MoneyManager.model.Income;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.CategoryRepo;
import com.example.MoneyManager.repository.ExpenseRepo;
import com.example.MoneyManager.repository.IncomeRepo;
import com.example.MoneyManager.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class GuestDataSeeder implements ApplicationRunner {

    @Value("${guest.email}")
    private String guestEmail;

    @Value("${guest.password}")
    private String guestPassword;

    private final ProfileRepo profileRepo;
    private final CategoryRepo categoryRepo;
    private final ExpenseRepo expenseRepo;
    private final IncomeRepo incomeRepo;
    private final PasswordEncoder passwordEncoder;

    public GuestDataSeeder(ProfileRepo profileRepo, CategoryRepo categoryRepo,
                           ExpenseRepo expenseRepo, IncomeRepo incomeRepo,
                           PasswordEncoder passwordEncoder) {
        this.profileRepo = profileRepo;
        this.categoryRepo = categoryRepo;
        this.expenseRepo = expenseRepo;
        this.incomeRepo = incomeRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (profileRepo.findByEmail(guestEmail).isPresent()) {
            return; // already seeded
        }

        Profile guest = Profile.builder()
                .fullname("Guest User")
                .email(guestEmail)
                .password(passwordEncoder.encode(guestPassword))
                .isActive(true)
                .build();
        guest = profileRepo.save(guest);

        Category salary = categoryRepo.save(Category.builder()
                .name("Salary").type("income").icon("💰").profile(guest).build());
        Category freelance = categoryRepo.save(Category.builder()
                .name("Freelance").type("income").icon("💻").profile(guest).build());
        Category food = categoryRepo.save(Category.builder()
                .name("Food").type("expense").icon("🍔").profile(guest).build());
        Category rent = categoryRepo.save(Category.builder()
                .name("Rent").type("expense").icon("🏠").profile(guest).build());

        incomeRepo.save(Income.builder().name("Monthly Salary").amount(new BigDecimal("50000"))
                .date(LocalDate.now().minusDays(5)).category(salary).profile(guest).build());
        incomeRepo.save(Income.builder().name("Freelance Project").amount(new BigDecimal("12000"))
                .date(LocalDate.now().minusDays(2)).category(freelance).profile(guest).build());

        expenseRepo.save(Expense.builder().name("Groceries").amount(new BigDecimal("2500"))
                .date(LocalDate.now().minusDays(3)).category(food).profile(guest).build());
        expenseRepo.save(Expense.builder().name("Dining Out").amount(new BigDecimal("800"))
                .date(LocalDate.now().minusDays(1)).category(food).profile(guest).build());
        expenseRepo.save(Expense.builder().name("Monthly Rent").amount(new BigDecimal("15000"))
                .date(LocalDate.now().minusDays(6)).category(rent).profile(guest).build());
    }
}