package bank.data_operations.importer;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvDataImporter extends DataImporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    protected List<BankAccount> parseAccounts(String filePath) {
        List<BankAccount> accounts = new ArrayList<>();
        Path accountsPath = Paths.get(filePath, "accounts.csv");
        if (!accountsPath.toFile().exists()) {
            return accounts;
        }
        try (CSVReader reader = new CSVReader(new FileReader(accountsPath.toFile()))) {
            List<String[]> lines = reader.readAll();
            if (lines.isEmpty()) return accounts;
            // Пропускаем заголовок
            for (int i = 1; i < lines.size(); i++) {
                String[] line = lines.get(i);
                if (line.length < 5) continue;
                BankAccount acc = new BankAccount(line[1], line[3]);
                acc.setId(line[0]);
                acc.setBalance(new BigDecimal(line[2]));
                acc.setCreatedAt(LocalDateTime.parse(line[4], DATE_FORMATTER));
                accounts.add(acc);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Ошибка парсинга accounts.csv", e);
        }
        return accounts;
    }

    @Override
    protected List<Category> parseCategories(String filePath) {
        List<Category> categories = new ArrayList<>();
        Path catPath = Paths.get(filePath, "categories.csv");
        if (!catPath.toFile().exists()) {
            return categories;
        }
        try (CSVReader reader = new CSVReader(new FileReader(catPath.toFile()))) {
            List<String[]> lines = reader.readAll();
            if (lines.isEmpty()) return categories;
            for (int i = 1; i < lines.size(); i++) {
                String[] line = lines.get(i);
                if (line.length < 5) continue;
                Category cat = new Category(line[1], OperationType.valueOf(line[2]), line[3]);
                cat.setId(line[0]);
                cat.setCreatedAt(LocalDateTime.parse(line[4], DATE_FORMATTER));
                categories.add(cat);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Ошибка парсинга categories.csv", e);
        }
        return categories;
    }

    @Override
    protected List<Operation> parseOperations(String filePath) {
        List<Operation> operations = new ArrayList<>();
        Path opPath = Paths.get(filePath, "operations.csv");
        if (!opPath.toFile().exists()) {
            return operations;
        }
        try (CSVReader reader = new CSVReader(new FileReader(opPath.toFile()))) {
            List<String[]> lines = reader.readAll();
            if (lines.isEmpty()) return operations;
            for (int i = 1; i < lines.size(); i++) {
                String[] line = lines.get(i);
                if (line.length < 7) continue;

                BankAccount account = accountRepository.findById(line[2]).orElse(null);
                if (account == null) continue;

                Category category = line[6].isEmpty() ? null : categoryRepository.findById(line[6]).orElse(null);

                Operation op = new Operation(
                        OperationType.valueOf(line[1]),
                        account,
                        new BigDecimal(line[3]),
                        line[5],
                        category
                );
                op.setId(line[0]);
                op.setDate(LocalDateTime.parse(line[4], DATE_FORMATTER));
                operations.add(op);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Ошибка парсинга operations.csv", e);
        }
        return operations;
    }
}