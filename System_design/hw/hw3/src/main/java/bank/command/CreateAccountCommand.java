package bank.command;

import bank.facade.AccountFacade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public class CreateAccountCommand extends BaseCommand {
    private final AccountFacade accountFacade;
    private final String name;
    private final String currency;
    @Getter
    private String createdAccountId;

    @Override
    public void execute() {
        validateNotExecuted();
        var account = accountFacade.createAccount(name, currency);
        this.createdAccountId = account.getId();
        this.executed = true;
        System.out.println("Создан счет с ID: " + createdAccountId);
    }

    @Override
    public String getDescription() {
        return "Создание счета '" + name + "' в валюте " + currency;
    }

}
