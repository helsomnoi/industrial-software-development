package bank.command;

import bank.domain.BankAccount;
import bank.facade.AccountFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountCommandTest {

    @Mock
    private AccountFacade accountFacade;

    @Test
    void execute_ShouldCallFacade() {
        BankAccount account = new BankAccount("Тест", "RUB");
        account.setId("acc-1");
        when(accountFacade.createAccount("Тест", "RUB")).thenReturn(account);

        CreateAccountCommand command = new CreateAccountCommand(accountFacade, "Тест", "RUB");
        command.execute();

        assertTrue(command.isExecuted());
        assertEquals("acc-1", command.getCreatedAccountId());
        verify(accountFacade).createAccount("Тест", "RUB");
    }

    @Test
    void execute_Twice_ShouldThrowException() {
        when(accountFacade.createAccount(anyString(), anyString())).thenReturn(new BankAccount("Тест", "RUB"));
        CreateAccountCommand command = new CreateAccountCommand(accountFacade, "Тест", "RUB");
        command.execute();
        assertThrows(IllegalStateException.class, command::execute);
    }

    @Test
    void getDescription_ShouldReturnString() {
        CreateAccountCommand command = new CreateAccountCommand(accountFacade, "Тест", "RUB");
        assertEquals("Создание счета 'Тест' в валюте RUB", command.getDescription());
    }
}