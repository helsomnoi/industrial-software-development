package bank.data_operations.importer;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractStructuredDataImporter extends DataImporter {

    private final ObjectMapper mapper;

    public AbstractStructuredDataImporter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected List<BankAccount> parseAccounts(String filePath) {
        return parseFile(filePath, getAccountsFileName(), BankAccount[].class);
    }

    @Override
    protected List<Category> parseCategories(String filePath) {
        return parseFile(filePath, getCategoriesFileName(), Category[].class);
    }

    @Override
    protected List<Operation> parseOperations(String filePath) {
        List<Operation> operations = parseFile(filePath, getOperationsFileName(), Operation[].class);
        List<Operation> validOperations = new ArrayList<>();
        operations.forEach(op -> {
            BankAccount account = accountRepository.findById(op.getBankAccountId()).orElse(null);
            if (account == null) {
                System.err.println("Пропущена операция: счет не найден " + op.getBankAccountId());
                return;
            }
            Category category = null;
            if (op.getCategoryId() != null) {
                category = categoryRepository.findById(op.getCategoryId()).orElse(null);
                // Если категория не найдена, всё равно продолжаем (категория будет null)
            }
            op.setBankAccount(account);
            op.setCategory(category);
            validOperations.add(op);
        });
        return validOperations;
    }

    private <T> List<T> parseFile(String filePath, String fileName, Class<T[]> arrayClass) {
        try {
            Path fullPath = Paths.get(filePath, fileName);
            File file = fullPath.toFile();
            if (file.exists()) {
                T[] array = mapper.readValue(file, arrayClass);
                return Arrays.asList(array);
            }
            return List.of();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка парсинга " + fileName + ": " + e.getMessage(), e);
        }
    }

    protected abstract String getAccountsFileName();
    protected abstract String getCategoriesFileName();
    protected abstract String getOperationsFileName();
}