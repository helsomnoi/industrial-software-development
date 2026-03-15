package bank.dto;

import bank.domain.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class AnalyticsReport {
    private BankAccount account;
    private LocalDateTime from;
    private LocalDateTime to;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private Map<String, BigDecimal> incomeByCategory;
    private Map<String, BigDecimal> expenseByCategory;

    public BigDecimal getNetBalance() {
        return totalIncome.subtract(totalExpense);
    }

    public void print() {
        System.out.println("\nАНАЛИТИКА ПО СЧЕТУ: " + account.getName());
        System.out.println("Период: " + from.toLocalDate() + " - " + to.toLocalDate());
        System.out.println("===========================================");
        System.out.println("Доходы: " + totalIncome + " " + account.getCurrency());
        System.out.println("Расходы: " + totalExpense + " " + account.getCurrency());
        System.out.println("Итого: " + getNetBalance() + " " + account.getCurrency());
        System.out.println("\nПО КАТЕГОРИЯМ:");
        System.out.println("Доходы по категориям:");
        incomeByCategory.forEach((cat, sum) -> System.out.println("  " + cat + ": " + sum));
        System.out.println("Расходы по категориям:");
        expenseByCategory.forEach((cat, sum) -> System.out.println("  " + cat + ": " + sum));
        System.out.println("===========================================\n");
    }
}
