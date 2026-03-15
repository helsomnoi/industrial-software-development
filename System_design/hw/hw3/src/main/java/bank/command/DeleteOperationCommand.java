package bank.command;

import bank.facade.OperationFacade;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteOperationCommand extends BaseCommand {
    private final OperationFacade operationFacade;
    private final String operationId;

    @Override
    public void execute() {
        validateNotExecuted();
        operationFacade.deleteOperation(operationId);
        this.executed = true;
        System.out.println("Операция с ID " + operationId + " удалена");
    }

    @Override
    public String getDescription() {
        return "Удаление операции " + operationId;
    }
}