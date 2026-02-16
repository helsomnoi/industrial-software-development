package bank.service;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ImportExportService {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;
    private final ObjectMapper jsonMapper;
    private final YAMLMapper yamlMapper;
    private final String exportPath;

    public ImportExportService(AccountRepository accountRepository,
                               CategoryRepository categoryRepository,
                               OperationRepository operationRepository,
                               ObjectMapper jsonMapper,
                               YAMLMapper yamlMapper,
                               @Value("${bank.data.export-path:./exports}") String exportPath) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
        this.jsonMapper = jsonMapper;
        this.yamlMapper = yamlMapper;
        this.exportPath = exportPath;
        createExportDirectory();
    }

    private void createExportDirectory() {
        try {
            Files.createDirectories(Paths.get(exportPath));
        } catch (IOException e) {
            System.err.println("Не удалось создать директорию для экспорта: " + e.getMessage());
        }
    }

    // ========== JSON ==========
    public void exportToJson() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path exportDir = Paths.get(exportPath, "json_" + timestamp);
        try {
            Files.createDirectories(exportDir);
            jsonMapper.writeValue(new File(exportDir.toString(), "accounts.json"), accountRepository.findAll());
            jsonMapper.writeValue(new File(exportDir.toString(), "categories.json"), categoryRepository.findAll());
            jsonMapper.writeValue(new File(exportDir.toString(), "operations.json"), operationRepository.findAll());
            System.out.println("Данные экспортированы в JSON: " + exportDir);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте в JSON: " + e.getMessage(), e);
        }
    }

    public void importFromJson(String importPath) {
        try {
            Path importDir = Paths.get(importPath);
            if (!Files.exists(importDir) || !Files.isDirectory(importDir)) {
                throw new IllegalArgumentException("Указанная директория не существует: " + importPath);
            }

            // Импорт счетов
            File accountsFile = new File(importDir.toString(), "accounts.json");
            if (accountsFile.exists()) {
                BankAccount[] accounts = jsonMapper.readValue(accountsFile, BankAccount[].class);
                for (BankAccount acc : accounts) {
                    accountRepository.save(acc);
                }
                System.out.println("Импортировано счетов: " + accounts.length);
            }

            // Импорт категорий
            File categoriesFile = new File(importDir.toString(), "categories.json");
            if (categoriesFile.exists()) {
                Category[] categories = jsonMapper.readValue(categoriesFile, Category[].class);
                for (Category cat : categories) {
                    categoryRepository.save(cat);
                }
                System.out.println("Импортировано категорий: " + categories.length);
            }

            // Импорт операций
            File operationsFile = new File(importDir.toString(), "operations.json");
            if (operationsFile.exists()) {
                Operation[] operations = jsonMapper.readValue(operationsFile, Operation[].class);
                for (Operation op : operations) {
                    operationRepository.save(op);
                }
                System.out.println("Импортировано операций: " + operations.length);
            }

            System.out.println("Импорт из JSON завершен");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при импорте из JSON: " + e.getMessage(), e);
        }
    }

    // ========== YAML ==========
    public void exportToYaml() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path exportDir = Paths.get(exportPath, "yaml_" + timestamp);
            Files.createDirectories(exportDir);

            yamlMapper.writeValue(new File(exportDir.toString(), "accounts.yaml"), accountRepository.findAll());
            yamlMapper.writeValue(new File(exportDir.toString(), "categories.yaml"), categoryRepository.findAll());
            yamlMapper.writeValue(new File(exportDir.toString(), "operations.yaml"), operationRepository.findAll());

            System.out.println("Данные экспортированы в YAML: " + exportDir);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте в YAML: " + e.getMessage(), e);
        }
    }


    // ========== CSV ==========
    public void exportToCsv() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path exportDir = Paths.get(exportPath, "csv_" + timestamp);
            Files.createDirectories(exportDir);

            // Экспорт счетов
            try (CSVWriter writer = new CSVWriter(new FileWriter(exportDir + "/accounts.csv"))) {
                writer.writeNext(new String[]{"ID", "Name", "Balance", "Currency", "CreatedAt"});
                accountRepository.findAll().forEach(acc -> writer.writeNext(new String[]{
                        acc.getId(),
                        acc.getName(),
                        acc.getBalance().toString(),
                        acc.getCurrency(),
                        acc.getCreatedAt().toString()
                }));
            }

            // Экспорт категорий
            try (CSVWriter writer = new CSVWriter(new FileWriter(exportDir + "/categories.csv"))) {
                writer.writeNext(new String[]{"ID", "Name", "Type", "Description", "CreatedAt"});
                categoryRepository.findAll().forEach(cat -> writer.writeNext(new String[]{
                        cat.getId(),
                        cat.getName(),
                        cat.getType().toString(),
                        cat.getDescription(),
                        cat.getCreatedAt().toString()
                }));
            }

            // Экспорт операций
            try (CSVWriter writer = new CSVWriter(new FileWriter(exportDir + "/operations.csv"))) {
                writer.writeNext(new String[]{"ID", "Type", "AccountID", "Amount", "Date", "Description", "CategoryID"});
                operationRepository.findAll().forEach(op -> writer.writeNext(new String[]{
                        op.getId(),
                        op.getType().toString(),
                        op.getBankAccountId(),
                        op.getAmount().toString(),
                        op.getDate().toString(),
                        op.getDescription(),
                        op.getCategoryId() != null ? op.getCategoryId() : ""
                }));
            }

            System.out.println("Данные экспортированы в CSV: " + exportDir);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте в CSV: " + e.getMessage(), e);
        }
    }
}