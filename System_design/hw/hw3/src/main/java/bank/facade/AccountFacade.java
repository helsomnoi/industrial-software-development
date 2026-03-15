package bank.facade;

import bank.domain.BankAccount;
import bank.repository.AccountRepository;
import bank.factory.Impl.BankAccountFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AccountFacade {
    private final AccountRepository accountRepository;
    private final BankAccountFactory accountFactory;


    public BankAccount createAccount(String name, String currency) {
        validateAccountName(name);
        BankAccount account = accountFactory.create(name, currency);
        return accountRepository.save(account);
    }

    public BankAccount updateAccount(String accountId, String newName) {
        BankAccount account = getAccount(accountId);
        account.setName(newName);
        return accountRepository.save(account);
    }

    public void deleteAccount(String accountId) {
        BankAccount account = getAccount(accountId);
        accountRepository.deleteById(accountId);
    }

    public BankAccount getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет не найден: " + accountId));
    }

    public List<BankAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void recalculateBalance(String accountId) {
        BankAccount account = getAccount(accountId);
        account.recalculateBalance();
        accountRepository.save(account);
    }

    private void validateAccountName(String name) {
        if (accountRepository.existsByName(name)) {
            throw new IllegalArgumentException("Счет с именем '" + name + "' уже существует");
        }
    }
}