package bank.facade;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.dto.AnalyticsReport;
import bank.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsFacadeTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private AccountFacade accountFacade;

    @Mock
    private CategoryFacade categoryFacade;

    @InjectMocks
    private AnalyticsFacade analyticsFacade;

    private BankAccount account;
    private Category incomeCat;
    private Category expenseCat;
    private Operation incomeOp1;
    private Operation incomeOp2;
    private Operation expenseOp;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Счет", "RUB");
        account.setId("acc-1");
        incomeCat = new Category("Зарплата", OperationType.INCOME, "");
        incomeCat.setId("inc-1");
        expenseCat = new Category("Еда", OperationType.EXPENSE, "");
        expenseCat.setId("exp-1");

        incomeOp1 = new Operation(OperationType.INCOME, account, new BigDecimal("1000"), "зп", incomeCat);
        incomeOp1.setId("op1");
        incomeOp2 = new Operation(OperationType.INCOME, account, new BigDecimal("500"), "бонус", incomeCat);
        incomeOp2.setId("op2");
        expenseOp = new Operation(OperationType.EXPENSE, account, new BigDecimal("300"), "продукты", expenseCat);
        expenseOp.setId("op3");

        incomeOp1.setDate(LocalDateTime.now().minusDays(2));
        incomeOp2.setDate(LocalDateTime.now().minusDays(1));
        expenseOp.setDate(LocalDateTime.now());
    }

    @Test
    void generateReport_ShouldReturnCorrectReport() {
        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        LocalDateTime to = LocalDateTime.now();
        List<Operation> operations = List.of(incomeOp1, incomeOp2, expenseOp);

        when(accountFacade.getAccount("acc-1")).thenReturn(account);
        when(operationRepository.findByAccountIdAndDateBetween("acc-1", from, to)).thenReturn(operations);
        when(categoryFacade.getCategory("inc-1")).thenReturn(incomeCat);
        when(categoryFacade.getCategory("exp-1")).thenReturn(expenseCat);

        AnalyticsReport report = analyticsFacade.generateReport("acc-1", from, to);

        assertNotNull(report);
        assertEquals(account, report.getAccount());
        assertEquals(0, new BigDecimal("1500").compareTo(report.getTotalIncome()));
        assertEquals(0, new BigDecimal("300").compareTo(report.getTotalExpense()));
        assertEquals(0, new BigDecimal("1200").compareTo(report.getNetBalance()));
        assertEquals(1, report.getIncomeByCategory().size());
        assertEquals(0, new BigDecimal("1500").compareTo(report.getIncomeByCategory().get("Зарплата")));
        assertEquals(1, report.getExpenseByCategory().size());
        assertEquals(0, new BigDecimal("300").compareTo(report.getExpenseByCategory().get("Еда")));
    }

    @Test
    void generateReport_NoOperations_ShouldReturnZeros() {
        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        LocalDateTime to = LocalDateTime.now();

        when(accountFacade.getAccount("acc-1")).thenReturn(account);
        when(operationRepository.findByAccountIdAndDateBetween("acc-1", from, to)).thenReturn(List.of());

        AnalyticsReport report = analyticsFacade.generateReport("acc-1", from, to);

        assertEquals(BigDecimal.ZERO, report.getTotalIncome());
        assertEquals(BigDecimal.ZERO, report.getTotalExpense());
        assertTrue(report.getIncomeByCategory().isEmpty());
        assertTrue(report.getExpenseByCategory().isEmpty());
    }
}