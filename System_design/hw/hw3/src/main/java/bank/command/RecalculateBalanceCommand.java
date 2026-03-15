package bank.command;

import bank.facade.AccountFacade;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@AllArgsConstructor
public class RecalculateBalanceCommand extends BaseCommand {
    private final AccountFacade accountFacade;
    private final String accountId;

    @Override
    public void execute() {
        validateNotExecuted();
        accountFacade.recalculateBalance(accountId);
        this.executed = true;
        System.out.println("Баланс счета пересчитан");
    }

    @Override
    public String getDescription() {
        return "Пересчет баланса счета " + accountId;
    }
}