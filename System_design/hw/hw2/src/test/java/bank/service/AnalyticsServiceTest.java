package bank.service;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private BankAccount account;
    private Category incomeCat;
    private Category expenseCat;
    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Тест", "RUB");
        account.setId("acc-1");
        incomeCat = new Category("Зарплата", OperationType.INCOME, "");
        incomeCat.setId("inc-1");
        expenseCat = new Category("Еда", OperationType.EXPENSE, "");
        expenseCat.setId("exp-1");

        from = LocalDateTime.now().minusMonths(1);
        to = LocalDateTime.now();
    }

    @Test
    void printAnalytics_ShouldOutputCorrectData() {
        // given
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("1000"), "зп", incomeCat);
        op1.setDate(from.plusDays(1));
        Operation op2 = new Operation(OperationType.EXPENSE, account, new BigDecimal("300"), "продукты", expenseCat);
        op2.setDate(from.plusDays(2));
        Operation op3 = new Operation(OperationType.EXPENSE, account, new BigDecimal("200"), "кафе", expenseCat);
        op3.setDate(from.plusDays(3));
        List<Operation> operations = List.of(op1, op2, op3);

        when(operationRepository.findByAccountIdAndDateBetween("acc-1", from, to)).thenReturn(operations);
        when(categoryRepository.findById("inc-1")).thenReturn(Optional.of(incomeCat));
        when(categoryRepository.findById("exp-1")).thenReturn(Optional.of(expenseCat));

        // when
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        analyticsService.printAnalytics("acc-1", from, to);

        // then
        String output = out.toString();
        assertTrue(output.contains("Доходы: 1000.00 RUB"));
        assertTrue(output.contains("Расходы: 500.00 RUB"));
        assertTrue(output.contains("Итого: 500.00 RUB"));
        assertTrue(output.contains("Доходы по категориям:"));
        assertTrue(output.contains("Зарплата: 1000.00"));
        assertTrue(output.contains("Расходы по категориям:"));
        assertTrue(output.contains("Еда: 500.00"));

        System.setOut(System.out);
    }

    @Test
    void printAnalytics_WhenNoOperations_ShouldOutputZeros() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        when(operationRepository.findByAccountIdAndDateBetween("acc-1", from, to)).thenReturn(List.of());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        analyticsService.printAnalytics("acc-1", from, to);

        String output = out.toString();
        assertTrue(output.contains("Доходы: 0 RUB"));
        assertTrue(output.contains("Расходы: 0 RUB"));
        assertTrue(output.contains("Итого: 0 RUB"));

        System.setOut(System.out);
    }

    @Test
    void printAnalytics_AccountNotFound_ShouldThrow() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> analyticsService.printAnalytics("acc-1", from, to));
    }

    @Test
    void printAnalytics_WhenCategoryNotFound_ShouldShowWithoutCategory() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        op.setCategoryId("missing-cat");
        when(operationRepository.findByAccountIdAndDateBetween("acc-1", from, to))
                .thenReturn(List.of(op));
        when(categoryRepository.findById("missing-cat")).thenReturn(Optional.empty());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        analyticsService.printAnalytics("acc-1", from, to);

        String output = out.toString();
        assertTrue(output.contains("Без категории: 100.00")); // возможно, так
    }

    @Test
    void printAnalytics_WhenLessThanFiveOperations_ShouldShowAll() {
        // given
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест1", null);
        op1.setDate(from.plusDays(1));
        Operation op2 = new Operation(OperationType.EXPENSE, account, new BigDecimal("50"), "тест2", null);
        op2.setDate(from.plusDays(2));
        when(operationRepository.findByAccountIdAndDateBetween("acc-1", from, to)).thenReturn(List.of(op1, op2));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        analyticsService.printAnalytics("acc-1", from, to);

        String output = out.toString();
        assertTrue(output.contains("ТОП-5 КРУПНЫХ ОПЕРАЦИЙ"));
        assertTrue(output.contains("100.00 RUB"));
        assertTrue(output.contains("50.00 RUB"));
        System.setOut(System.out);
    }
}