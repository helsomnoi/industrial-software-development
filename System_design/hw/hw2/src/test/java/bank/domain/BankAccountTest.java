package bank.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    private BankAccount account;
    private Category incomeCategory;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Тестовый счет", "RUB");
        incomeCategory = new Category("Доход", OperationType.INCOME, "");
        expenseCategory = new Category("Расход", OperationType.EXPENSE, "");
    }

    @Test
    void constructor_ShouldCreateAccountWithZeroBalance() {
        assertEquals("Тестовый счет", account.getName());
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
        assertEquals("RUB", account.getCurrency());
        assertNotNull(account.getId());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
        assertTrue(account.getOperations().isEmpty());
    }

    @Test
    void deposit_ShouldIncreaseBalance() {
        account.deposit(new BigDecimal("1000.50"));
        assertEquals(0, new BigDecimal("1000.50").compareTo(account.getBalance()));

        account.deposit(new BigDecimal("499.50"));
        assertEquals(0, new BigDecimal("1500.00").compareTo(account.getBalance()));
    }

    @Test
    void deposit_ZeroAmount_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.deposit(BigDecimal.ZERO));
        assertEquals("Сумма пополнения должна быть положительной", exception.getMessage());
    }

    @Test
    void deposit_NegativeAmount_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("-100")));
        assertEquals("Сумма пополнения должна быть положительной", exception.getMessage());
    }

    @Test
    void withdraw_ShouldDecreaseBalance() {
        account.deposit(new BigDecimal("1000"));
        account.withdraw(new BigDecimal("300.50"));
        assertEquals(0, new BigDecimal("699.50").compareTo(account.getBalance()));
    }

    @Test
    void withdraw_InsufficientFunds_ShouldThrowException() {
        account.deposit(new BigDecimal("100"));
        Exception exception = assertThrows(IllegalStateException.class,
                () -> account.withdraw(new BigDecimal("200")));
        assertTrue(exception.getMessage().contains("Недостаточно средств"));
    }

    @Test
    void withdraw_ZeroAmount_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(BigDecimal.ZERO));
        assertEquals("Сумма снятия должна быть положительной", exception.getMessage());
    }

    @Test
    void withdraw_NegativeAmount_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("-50")));
        assertEquals("Сумма снятия должна быть положительной", exception.getMessage());
    }

    @Test
    void withdraw_ExactBalance_ShouldWork() {
        account.deposit(new BigDecimal("500"));
        account.withdraw(new BigDecimal("500"));
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }

    @Test
    void addOperation_ShouldAddToListAndUpdateBalance() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("500"), "тест", incomeCategory);
        account.addOperation(op);

        List<Operation> ops = account.getOperations();
        assertEquals(1, ops.size());
        assertEquals(op, ops.get(0));
        assertEquals(0, new BigDecimal("500").compareTo(account.getBalance()));
        assertNotNull(account.getUpdatedAt());
    }

    @Test
    void addOperation_ShouldUpdateTimestamp() {
        LocalDateTime before = account.getUpdatedAt();
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        account.addOperation(op);
        assertTrue(account.getUpdatedAt().isAfter(before));
    }

    @Test
    void recalculateBalance_WhenNoOperations_ShouldRemainZero() {
        account.setBalance(new BigDecimal("100"));
        account.recalculateBalance();
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }

    @Test
    void recalculateBalance_WithOperations_ShouldCorrectBalance() {
        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("1000"), "доход", incomeCategory);
        Operation op2 = new Operation(OperationType.EXPENSE, account, new BigDecimal("300"), "расход", expenseCategory);
        account.addOperation(op1);
        account.addOperation(op2);

        // Баланс уже обновился при добавлении, сбрасываем его для проверки
        account.setBalance(new BigDecimal("500"));
        account.recalculateBalance();
        assertEquals(0, new BigDecimal("700").compareTo(account.getBalance()));
    }

    @Test
    void recalculateBalance_WhenBalanceAlreadyCorrect_ShouldNotChange() {
        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("1000"), "доход", incomeCategory);
        Operation op2 = new Operation(OperationType.EXPENSE, account, new BigDecimal("300"), "расход", expenseCategory);
        account.addOperation(op1);
        account.addOperation(op2);

        BigDecimal correct = account.getBalance();
        account.recalculateBalance();
        assertEquals(0, correct.compareTo(account.getBalance()));
    }

    @Test
    void autoRecalculateIfNeeded_WhenDifferenceSmall_ShouldNotRecalculate() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        account.addOperation(op);
        account.setBalance(new BigDecimal("99.99"));
        boolean recalculated = account.autoRecalculateIfNeeded();
        assertFalse(recalculated);
        assertEquals(0, new BigDecimal("99.99").compareTo(account.getBalance()));
    }

    @Test
    void autoRecalculateIfNeeded_WhenDifferenceExactlyThreshold_ShouldNotRecalculate() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        account.addOperation(op);
        account.setBalance(new BigDecimal("99.99")); // разница 0.01
        boolean recalculated = account.autoRecalculateIfNeeded();
        assertFalse(recalculated);
    }

    @Test
    void autoRecalculateIfNeeded_WhenDifferenceBig_ShouldRecalculate() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        account.addOperation(op);
        account.setBalance(new BigDecimal("90"));
        boolean recalculated = account.autoRecalculateIfNeeded();
        assertTrue(recalculated);
        assertEquals(0, new BigDecimal("100").compareTo(account.getBalance()));
    }

    @Test
    void autoRecalculateIfNeeded_WhenDifferenceBigAndNegative_ShouldRecalculate() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        account.addOperation(op);
        account.setBalance(new BigDecimal("110")); // завышен
        boolean recalculated = account.autoRecalculateIfNeeded();
        assertTrue(recalculated);
        assertEquals(0, new BigDecimal("100").compareTo(account.getBalance()));
    }

    @Test
    void deposit_NullAmount_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.deposit(null));
        assertEquals("Сумма пополнения должна быть положительной", exception.getMessage());
    }

    @Test
    void withdraw_NullAmount_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(null));
        assertEquals("Сумма снятия должна быть положительной", exception.getMessage());
    }

    @Test
    void autoRecalculateIfNeeded_WhenNoOperations_ShouldRecalculateToZero() {
        account.setBalance(new BigDecimal("100"));
        boolean recalculated = account.autoRecalculateIfNeeded();
        assertTrue(recalculated);
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }


    @Test
    void autoRecalculateIfNeeded_WhenOperationsButBalanceCorrect_ShouldNotRecalculate() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        account.addOperation(op);
        account.setBalance(new BigDecimal("100"));
        boolean recalculated = account.autoRecalculateIfNeeded();
        assertFalse(recalculated);
        assertEquals(0, new BigDecimal("100.00").compareTo(account.getBalance()));
    }

    @Test
    void recalculateBalance_WhenNoOperations_ShouldSetBalanceToZero() {
        account.setBalance(new BigDecimal("100"));
        account.recalculateBalance();
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }

}