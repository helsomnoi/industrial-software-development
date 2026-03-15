package bank.command;

import bank.facade.OperationFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteOperationCommandTest {

    @Mock
    private OperationFacade operationFacade;

    @Test
    void execute_ShouldCallFacade() {
        DeleteOperationCommand command = new DeleteOperationCommand(operationFacade, "op-1");
        command.execute();

        verify(operationFacade).deleteOperation("op-1");
        assertTrue(command.isExecuted());
    }

    @Test
    void execute_Twice_ShouldThrow() {
        DeleteOperationCommand command = new DeleteOperationCommand(operationFacade, "op-1");
        command.execute();
        assertThrows(IllegalStateException.class, command::execute);
    }
}