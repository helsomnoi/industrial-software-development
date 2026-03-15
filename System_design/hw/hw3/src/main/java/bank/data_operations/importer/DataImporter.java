package bank.data_operations.importer;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class DataImporter {
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected OperationRepository operationRepository;

    public final void importData(String filePath) {
        System.out.println("Начало импорта данных из файла: " + filePath);

        List<BankAccount> accounts = parseAccounts(filePath);
        List<Category> categories = parseCategories(filePath);
        List<Operation> operations = parseOperations(filePath);

        validateData(accounts, categories, operations);
        saveData(accounts, categories, operations);
        postProcess();
        System.out.println("Импорт успешно завершен");
    }

    protected abstract List<BankAccount> parseAccounts(String filePath);
    protected abstract List<Category> parseCategories(String filePath);
    protected abstract List<Operation> parseOperations(String filePath);

    protected void validateData(List<BankAccount> accounts,
                                List<Category> categories,
                                List<Operation> operations) {
        System.out.println(" Проверка целостности данных...");
    }

    protected void postProcess() {
        // Действия после импорта (например, пересчет балансов)
        System.out.println("  Выполнение пост-обработки...");
    }

    private void saveData(List<BankAccount> accounts,
                          List<Category> categories,
                          List<Operation> operations) {
        System.out.println("  Сохранение данных в репозитории...");

        accounts.forEach(account -> {
            if (!accountRepository.findById(account.getId()).isPresent()) {
                accountRepository.save(account);
            }
        });

        categories.forEach(category -> {
            if (!categoryRepository.findById(category.getId()).isPresent()) {
                categoryRepository.save(category);
            }
        });

        operations.forEach(operation -> {
            if (!operationRepository.findById(operation.getId()).isPresent()) {
                operationRepository.save(operation);
            }
        });
    }
}
