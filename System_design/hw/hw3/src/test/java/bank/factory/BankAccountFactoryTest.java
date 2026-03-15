package bank.factory;

import bank.domain.BankAccount;
import bank.factory.Impl.BankAccountFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountFactoryTest {

    private final BankAccountFactory factory = new BankAccountFactory();

    @Test
    void create_ValidParams_ShouldReturnAccount() {
        BankAccount account = factory.create("Основной", "RUB");
        assertNotNull(account);
        assertEquals("Основной", account.getName());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals("RUB", account.getCurrency());
        assertNotNull(account.getId());
    }

    @Test
    void create_EmptyName_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> factory.create("", "RUB"));
        assertEquals("Имя счета не может быть пустым", exception.getMessage());
    }

    @Test
    void create_NullName_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> factory.create(null, "RUB"));
        assertEquals("Имя счета не может быть пустым", exception.getMessage());
    }

    @Test
    void create_InsufficientParams_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> factory.create("Только имя"));
        assertEquals("Требуется имя и валюта", exception.getMessage());
    }
}