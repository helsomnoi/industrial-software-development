package bank.data_operations.exporter;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class DataExporter {

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected OperationRepository operationRepository;

    protected String exportPath = "./exports";


    public final void exportData() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path exportDir = createExportDirectory(timestamp);

        List<BankAccount> accounts = accountRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<Operation> operations = operationRepository.findAll();

        writeAccounts(exportDir, accounts);
        writeCategories(exportDir, categories);
        writeOperations(exportDir, operations);

        System.out.println("Данные экспортированы в " + exportDir);
    }

    protected Path createExportDirectory(String timestamp) {
        try {
            Path dir = Paths.get(exportPath, getFormat() + "_" + timestamp);
            Files.createDirectories(dir);
            return dir;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для экспорта", e);
        }
    }

    protected abstract String getFormat();
    protected abstract void writeAccounts(Path dir, List<BankAccount> accounts);
    protected abstract void writeCategories(Path dir, List<Category> categories);
    protected abstract void writeOperations(Path dir, List<Operation> operations);
}