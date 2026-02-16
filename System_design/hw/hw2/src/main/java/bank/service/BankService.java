package bank.service;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankService {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    public BankService(AccountRepository accountRepository,
                       CategoryRepository categoryRepository,
                       OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    // --- Accounts ---
    public BankAccount createAccount(String name, String currency) {
        if (accountRepository.existsByName(name)) {
            throw new IllegalArgumentException("Счет с именем '" + name + "' уже существует");
        }
        BankAccount account = new BankAccount(name, currency);
        return accountRepository.save(account);
    }

    public BankAccount updateAccount(String accountId, String newName) {
        BankAccount account = getAccount(accountId);
        account.setName(newName);
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    public void deleteAccount(String accountId) {
        BankAccount account = getAccount(accountId);
        // Удаляем все операции счёта (опционально)
        List<Operation> accountOps = operationRepository.findByAccountId(accountId);
        accountOps.forEach(op -> operationRepository.deleteById(op.getId()));
        accountRepository.deleteById(accountId);
    }

    public BankAccount getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет с ID " + accountId + " не найден"));
    }

    public List<BankAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    // --- Categories ---
    public Category createCategory(String name, OperationType type, String description) {
        if (categoryRepository.existsByNameAndType(name, type)) {
            throw new IllegalArgumentException("Категория с именем '" + name + "' и типом " + type + " уже существует");
        }
        Category category = new Category(name, type, description);
        return categoryRepository.save(category);
    }

    public Category updateCategory(String categoryId, String newName, String newDescription) {
        Category category = getCategory(categoryId);
        category.setName(newName);
        category.setDescription(newDescription);
        return categoryRepository.save(category);
    }

    public void deleteCategory(String categoryId) {
        Category category = getCategory(categoryId);
        boolean hasOperations = !operationRepository.findByCategoryId(categoryId).isEmpty();
        if (hasOperations) {
            throw new IllegalStateException("Нельзя удалить категорию, к которой привязаны операции");
        }
        categoryRepository.deleteById(categoryId);
    }

    public Category getCategory(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Категория с ID " + categoryId + " не найдена"));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoriesByType(OperationType type) {
        return categoryRepository.findByType(type);
    }

    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    public Operation createOperation(OperationType type, String accountId,
                                     BigDecimal amount, String description, String categoryId) {
        BankAccount account = getAccount(accountId);
        Category category = (categoryId != null) ? getCategory(categoryId) : null;

        if (category != null && category.getType() != type) {
            throw new IllegalArgumentException(
                    String.format("Тип операции (%s) не соответствует типу категории (%s)", type, category.getType()));
        }

        Operation operation = new Operation(type, account, amount, description, category);
        account.addOperation(operation);
        accountRepository.save(account);          // обновляем счёт (баланс уже изменился)
        return operationRepository.save(operation);
    }

    public List<Operation> getAccountOperations(String accountId, LocalDateTime from, LocalDateTime to) {
        return operationRepository.findByAccountIdAndDateBetween(accountId, from, to);
    }

    public void recalculateAccountBalance(String accountId) {
        BankAccount account = getAccount(accountId);
        account.recalculateBalance();
        accountRepository.save(account);
    }

    public void recalculateAllBalances() {
        List<BankAccount> accounts = accountRepository.findAll();
        accounts.forEach(BankAccount::recalculateBalance);
        accounts.forEach(accountRepository::save);
    }

    public void checkAllBalances() {
        List<BankAccount> accounts = accountRepository.findAll();
        System.out.println("\nПРОВЕРКА БАЛАНСОВ ВСЕХ СЧЕТОВ:");
        System.out.println("===================================");
        for (BankAccount account : accounts) {
            BigDecimal calculated = operationRepository.sumByAccountAndTypeAndDateBetween(
                            account.getId(), OperationType.INCOME, LocalDateTime.MIN, LocalDateTime.MAX)
                    .subtract(operationRepository.sumByAccountAndTypeAndDateBetween(
                            account.getId(), OperationType.EXPENSE, LocalDateTime.MIN, LocalDateTime.MAX));
            if (account.getBalance().compareTo(calculated) != 0) {
                System.out.printf("Счет '%s': баланс %s, расчетный %s (разница %s)%n",
                        account.getName(), account.getBalance(), calculated,
                        account.getBalance().subtract(calculated));
            } else {
                System.out.printf("Счет '%s': баланс корректен (%s)%n",
                        account.getName(), account.getBalance());
            }
        }
        System.out.println("===================================\n");
    }


}