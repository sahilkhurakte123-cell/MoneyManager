package com.example.MoneyManager.service;

import com.example.MoneyManager.dto.ExpenseDto;
import com.example.MoneyManager.model.Category;
import com.example.MoneyManager.model.Expense;
import com.example.MoneyManager.model.Profile;
import com.example.MoneyManager.repository.ExpenseRepo;
import com.example.MoneyManager.repository.ProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ExpenseService expenseService;
    private final ProfileRepo profileRepo;
    private final ActivationEmailService activationEmailService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 00 * * *", zone = "IST")
    public void sendDailyTransactionEmail() {
        log.info("Sending Daily Transaction Email");
         List<Profile> profiles = profileRepo.findAll();
         for(Profile profile : profiles) {
             String body =
                     "Hi " + profile.getFullname() + ",<br><br>"
                             + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                             + "<a href=\"" + frontendUrl + "\" "
                             + "style=\"display:inline-block;padding:10px 20px;background-color:#4CAF50;"
                             + "color:white;text-decoration:none;border-radius:6px;font-weight:600;\">"
                             + "Open Money Manager</a>"
                             + "<br><br>Best regards,<br>Money Manager Team";

             activationEmailService.sendEmail(profile.getEmail(), "Daily Transaction Email", body);

         }
    }

    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyExpenseEmail() {
        log.info("Sending Daily Expense Email");
        List<Profile> profiles = profileRepo.findAll();
        for(Profile profile : profiles) {
            List<ExpenseDto> expenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
            if(!expenses.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table style='border-collapse:collapse;width:100%;'>");

                sb.append("<tr style='background-color:#f2f2f2;'>"
                        + "<th style='border:1px solid #ddd;padding:8px;'>Name</th>"
                        + "<th style='border:1px solid #ddd;padding:8px;'>Amount</th>"
                        + "</tr>");
                int i = 1;
                for(ExpenseDto expenseDto : expenses) {
                    sb.append("<tr>");

                    sb.append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(i++)
                            .append("</td>");

                    sb.append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expenseDto.getName())
                            .append("</td>");

                    sb.append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expenseDto.getAmount())
                            .append("</td>");

                    sb.append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expenseDto.getCategoryId())
                            .append("</td>");

                    sb.append("</tr>");
                }
                sb.append("</table>");
                String body =
                        "Hi " + profile.getFullname() + ",<br/><br/>"
                                + "Here is a summary of your expenses for today:<br/><br/>"
                                + sb.toString()
                                + "<br/><br/>Best regards,<br/>Money Manager Team";
                activationEmailService.sendEmail(profile.getEmail(), "Expense Email", body);

            }
        }
    }

}
