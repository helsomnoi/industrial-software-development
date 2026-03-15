package bank.command;

import bank.domain.OperationType;
import bank.facade.OperationFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateOperationCommandTest {

    @Mock
    private OperationFacade operationFacade;

    @Test
    void execute_ShouldCallFacade() {
        CreateOperationCommand command = new CreateOperationCommand(
                operationFacade, OperationType.INCOME, "acc-1",
                new BigDecimal("1000"), "тест", "cat-1");
        command.execute();

        verify(operationFacade).createOperation(OperationType.INCOME, "acc-1",
                new BigDecimal("1000"), "тест", "cat-1");
    }
}