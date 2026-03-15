package bank.factory;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.factory.Impl.OperationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OperationFactoryTest {

    private final OperationFactory factory = new OperationFactory();
    private BankAccount account;
    private Category incomeCategory;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Счет", "RUB");
        incomeCategory = new Category("Доход", OperationType.INCOME, "");
        expenseCategory = new Category("Расход", OperationType.EXPENSE, "");
    }

    @Test
    void create_ValidParams_ShouldReturnOperation() {
        Operation op = factory.create(OperationType.INCOME, account, new BigDecimal("1000"), "описание", incomeCategory);
        assertNotNull(op);
        assertEquals(OperationType.INCOME, op.getType());
        assertEquals(account, op.getBankAccount());
        assertEquals(0, new BigDecimal("1000").compareTo(op.getAmount()));
        assertEquals(account, op.getBankAccount());
        assertEquals(incomeCategory, op.getCategory());
    }

    @Test
    void create_TypeMismatch_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> factory.create(OperationType.EXPENSE, account, new BigDecimal("500"), "трата", incomeCategory));
        assertTrue(exception.getMessage().contains("не соответствует типу категории"));
    }

    @Test
    void create_WithoutCategory_ShouldWork() {
        Operation op = factory.create(OperationType.INCOME, account, new BigDecimal("777"), "без кат", null);
        assertNotNull(op);
        assertNull(op.getCategory());
    }

    @Test
    void create_InsufficientParams_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> factory.create(OperationType.INCOME, account, new BigDecimal("100")));
        assertEquals("Требуется тип, счет, сумма, описание и категория", exception.getMessage());
    }
}