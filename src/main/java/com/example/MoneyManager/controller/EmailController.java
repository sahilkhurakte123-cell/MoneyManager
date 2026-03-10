package com.example.MoneyManager.controller;

import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ActivationEmailService emailService;
    private final ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcelDetails() throws IOException, MessagingException {
        Profile profile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos,incomeService.getCurrentMonthIncomesForCurrentUser());
        emailService.sendEmailWithAttatchment(profile.getEmail(), "Your Income Excel Report", "PFA your income report for current month", baos.toByteArray(), "income_report.xlsx");
        return ResponseEntity.ok(null);
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcelDetails() throws IOException, MessagingException {
        Profile profile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpenseToExcel(baos,expenseService.getCurrentMonthExpensesForCurrentUser());
        emailService.sendEmailWithAttatchment(profile.getEmail(), "Your Expense Excel Report", "PFA your expense report for current month", baos.toByteArray(), "expense_report.xlsx");
        return ResponseEntity.ok(null);
    }

}
