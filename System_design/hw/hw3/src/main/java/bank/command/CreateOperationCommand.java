package bank.command;

import bank.domain.OperationType;
import bank.facade.OperationFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@AllArgsConstructor
public class CreateOperationCommand extends BaseCommand {
    private final OperationFacade operationFacade;
    private final OperationType type;
    private final String accountId;
    private final BigDecimal amount;
    private final String description;
    private final String categoryId;

    @Override
    public void execute() {
        validateNotExecuted();
        operationFacade.createOperation(type, accountId, amount, description, categoryId);
        this.executed = true;
        System.out.println("Создана операция: " + type + " " + amount);
    }

    @Override
    public String getDescription() {
        return "Создание операции " + type + " на сумму " + amount;
    }
}
