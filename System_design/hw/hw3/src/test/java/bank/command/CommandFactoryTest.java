package bank.command;

import bank.domain.OperationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CommandFactoryTest {

    @Autowired
    private CommandFactory commandFactory;

    @Test
    void createCreateAccountCommand_ShouldReturnCommand() {
        Command cmd = commandFactory.createCreateAccountCommand("Тест1", "RUB");
        assertNotNull(cmd);
        assertEquals("Создание счета 'Тест1' в валюте RUB", cmd.getDescription());
    }

    @Test
    void createCreateCategoryCommand_ShouldReturnCommand() {
        Command cmd = commandFactory.createCreateCategoryCommand(
                "Кат", OperationType.INCOME, "описание");
        assertNotNull(cmd);
        assertEquals("Создание категории 'Кат' типа INCOME", cmd.getDescription());
    }

    @Test
    void createCreateOperationCommand_ShouldReturnCommand() {
        Command cmd = commandFactory.createCreateOperationCommand(
                OperationType.EXPENSE, "acc-1", new BigDecimal("100"), "desc", null);
        assertNotNull(cmd);
        assertTrue(cmd.getDescription().contains("Создание операции EXPENSE на сумму 100"));
    }

    @Test
    void createRecalculateBalanceCommand_ShouldReturnCommand() {
        Command cmd = commandFactory.createRecalculateBalanceCommand("acc-1");
        assertNotNull(cmd);
        assertEquals("Пересчет баланса счета acc-1", cmd.getDescription());
    }
}