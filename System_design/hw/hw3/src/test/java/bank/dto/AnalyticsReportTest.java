package bank.dto;

import bank.domain.BankAccount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnalyticsReportTest {

    @Test
    void constructor_ShouldCreateReport() {
        BankAccount account = new BankAccount("Тест", "RUB");
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(1);
        BigDecimal income = new BigDecimal("1000");
        BigDecimal expense = new BigDecimal("300");
        Map<String, BigDecimal> incomeByCat = Map.of("Зарплата", income);
        Map<String, BigDecimal> expenseByCat = Map.of("Еда", expense);

        AnalyticsReport report = new AnalyticsReport(
                account, from, to, income, expense, incomeByCat, expenseByCat);

        assertNotNull(report);
        assertEquals(account, report.getAccount());
        assertEquals(from, report.getFrom());
        assertEquals(to, report.getTo());
        assertEquals(income, report.getTotalIncome());
        assertEquals(expense, report.getTotalExpense());
        assertEquals(incomeByCat, report.getIncomeByCategory());
        assertEquals(expenseByCat, report.getExpenseByCategory());
    }

    @Test
    void getNetBalance_ShouldReturnDifference() {
        BankAccount account = new BankAccount("Тест", "RUB");
        AnalyticsReport report = new AnalyticsReport(
                account, LocalDateTime.now(), LocalDateTime.now(),
                new BigDecimal("1000"), new BigDecimal("300"),
                Map.of(), Map.of());

        assertEquals(0, new BigDecimal("700").compareTo(report.getNetBalance()));
    }

    @Test
    void print_ShouldNotThrow() {
        BankAccount account = new BankAccount("Тест", "RUB");
        AnalyticsReport report = new AnalyticsReport(
                account, LocalDateTime.now(), LocalDateTime.now(),
                new BigDecimal("1000"), new BigDecimal("300"),
                Map.of("Зарплата", new BigDecimal("1000")),
                Map.of("Еда", new BigDecimal("300")));

        report.print();
    }
}