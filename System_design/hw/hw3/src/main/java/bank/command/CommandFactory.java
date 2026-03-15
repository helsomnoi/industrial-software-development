package bank.command;

import bank.domain.OperationType;
import bank.facade.AccountFacade;
import bank.facade.CategoryFacade;
import bank.facade.OperationFacade;
import bank.facade.ExportFacade;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class CommandFactory {
    private final AccountFacade accountFacade;
    private final CategoryFacade categoryFacade;
    private final OperationFacade operationFacade;
    private final ExportFacade exportFacade;

    public CommandFactory(AccountFacade accountFacade,
                          CategoryFacade categoryFacade,
                          OperationFacade operationFacade,
                          ExportFacade exportFacade) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
        this.exportFacade = exportFacade;
    }

    public Command createCreateAccountCommand(String name, String currency) {
        Command cmd = new CreateAccountCommand(accountFacade, name, currency);
        return new TimedCommandDecorator(cmd);
    }

    public Command createCreateCategoryCommand(String name, OperationType type, String description) {
        Command cmd = new CreateCategoryCommand(categoryFacade, name, type, description);
        return new TimedCommandDecorator(cmd);
    }

    public Command createCreateOperationCommand(OperationType type, String accountId,
                                                BigDecimal amount, String description,
                                                String categoryId) {
        Command cmd = new CreateOperationCommand(operationFacade, type, accountId, amount, description, categoryId);
        return new TimedCommandDecorator(cmd);
    }

    public Command createDeleteOperationCommand(String operationId) {
        Command cmd = new DeleteOperationCommand(operationFacade, operationId);
        return new TimedCommandDecorator(cmd);
    }

    public Command createRecalculateBalanceCommand(String accountId) {
        Command cmd = new RecalculateBalanceCommand(accountFacade, accountId);
        return new TimedCommandDecorator(cmd);
    }
}