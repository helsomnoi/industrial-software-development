package bank.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OperationTest {

    private BankAccount account;
    private Category category;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Счет", "RUB");
        category = new Category("Категория", OperationType.INCOME, "Описание");
    }

    @Test
    void constructor_ShouldCreateOperationWithCorrectFields() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("1000"), "описание", category);

        assertNotNull(op.getId());
        assertEquals(OperationType.INCOME, op.getType());
        assertEquals(account.getId(), op.getBankAccountId());
        assertEquals(account, op.getBankAccount());
        assertEquals(0, new BigDecimal("1000").compareTo(op.getAmount()));
        assertEquals("описание", op.getDescription());
        assertEquals(category.getId(), op.getCategoryId());
        assertEquals(category, op.getCategory());
        assertNotNull(op.getDate());
        assertNotNull(op.getCreatedAt());
        assertTrue(op.getDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void constructor_WithoutCategory_ShouldWork() {
        Operation op = new Operation(OperationType.EXPENSE, account, new BigDecimal("500"), "без категории", null);

        assertNull(op.getCategoryId());
        assertNull(op.getCategory());
    }

    @Test
    void applyToAccount_Income_ShouldIncreaseBalance() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("500"), "доход", null);
        op.applyToAccount();
        assertEquals(0, new BigDecimal("500").compareTo(account.getBalance()));
    }

    @Test
    void applyToAccount_Expense_ShouldDecreaseBalance() {
        account.deposit(new BigDecimal("1000"));
        Operation op = new Operation(OperationType.EXPENSE, account, new BigDecimal("300"), "расход", null);
        op.applyToAccount();
        assertEquals(0, new BigDecimal("700").compareTo(account.getBalance()));
    }

    @Test
    void applyToAccount_ExpenseInsufficientFunds_ShouldThrow() {
        Operation op = new Operation(OperationType.EXPENSE, account, new BigDecimal("100"), "расход", null);
        assertThrows(IllegalStateException.class, op::applyToAccount);
    }

    @Test
    void applyToAccount_WithoutBankAccount_ShouldThrow() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        op.setBankAccount(null);
        assertThrows(IllegalStateException.class, op::applyToAccount);
    }

    @Test
    void setBankAccount_ShouldUpdateBankAccountAndId() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        BankAccount newAccount = new BankAccount("Новый", "USD");
        op.setBankAccount(newAccount);
        assertEquals(newAccount, op.getBankAccount());
        assertEquals(newAccount.getId(), op.getBankAccountId());
    }

    @Test
    void setCategory_ShouldUpdateCategoryAndId() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        Category newCat = new Category("Новая", OperationType.EXPENSE, "");
        op.setCategory(newCat);
        assertEquals(newCat, op.getCategory());
        assertEquals(newCat.getId(), op.getCategoryId());
    }

    @Test
    void date_ShouldBeSettable() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        LocalDateTime newDate = LocalDateTime.now().minusDays(5);
        op.setDate(newDate);
        assertEquals(newDate, op.getDate());
    }
}