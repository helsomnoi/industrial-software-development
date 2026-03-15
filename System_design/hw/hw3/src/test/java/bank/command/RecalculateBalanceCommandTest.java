package bank.command;

import bank.facade.AccountFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecalculateBalanceCommandTest {

    @Mock
    private AccountFacade accountFacade;

    @Test
    void execute_ShouldCallFacade() {
        RecalculateBalanceCommand command = new RecalculateBalanceCommand(accountFacade, "acc-1");
        command.execute();

        verify(accountFacade).recalculateBalance("acc-1");
    }
}