package bank.command;

import bank.domain.OperationType;
import bank.facade.CategoryFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@AllArgsConstructor
public class CreateCategoryCommand extends BaseCommand {
    private final CategoryFacade categoryFacade;
    private final String name;
    private final OperationType type;
    private final String description;

    @Override
    public void execute() {
        validateNotExecuted();
        categoryFacade.create(name, type, description);
        this.executed = true;
        System.out.println("Создана категория: " + name);
    }

    @Override
    public String getDescription() {
        return "Создание категории '" + name + "' типа " + type;
    }
}
