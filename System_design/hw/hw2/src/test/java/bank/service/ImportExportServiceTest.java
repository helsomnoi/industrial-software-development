package bank.service;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import bank.repository.inmemory.InMemoryAccountRepository;
import bank.repository.inmemory.InMemoryCategoryRepository;
import bank.repository.inmemory.InMemoryOperationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImportExportServiceTest {

    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;
    private OperationRepository operationRepository;
    private ObjectMapper jsonMapper;
    private YAMLMapper yamlMapper;
    private ImportExportService importExportService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Используем реальные репозитории, чтобы проверять сохранение
        accountRepository = mock(AccountRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        operationRepository = mock(OperationRepository.class);

        jsonMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);

        yamlMapper = (YAMLMapper) new YAMLMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        importExportService = new ImportExportService(
                accountRepository, categoryRepository, operationRepository,
                jsonMapper, yamlMapper, tempDir.toString());
    }

    // --- JSON экспорт ---
    @Test
    void exportToJson_ShouldCreateFiles() {
        // Создаём реальные in-memory репозитории
        AccountRepository realAccountRepo = new InMemoryAccountRepository();
        CategoryRepository realCategoryRepo = new InMemoryCategoryRepository();
        OperationRepository realOperationRepo = new InMemoryOperationRepository();

        BankAccount acc = new BankAccount("Тест", "RUB");
        realAccountRepo.save(acc);
        Category cat = new Category("Категория", OperationType.INCOME, "");
        realCategoryRepo.save(cat);
        Operation op = new Operation(OperationType.INCOME, acc, new BigDecimal("100"), "оп", cat);
        realOperationRepo.save(op);

        ImportExportService realService = new ImportExportService(
                realAccountRepo, realCategoryRepo, realOperationRepo,
                jsonMapper, yamlMapper, tempDir.toString());

        realService.exportToJson();

        File[] dirs = tempDir.toFile().listFiles((d, name) -> name.startsWith("json_"));
        assertNotNull(dirs);
        assertEquals(1, dirs.length);
        File exportDir = dirs[0];
        assertTrue(new File(exportDir, "accounts.json").exists());
        assertTrue(new File(exportDir, "categories.json").exists());
        assertTrue(new File(exportDir, "operations.json").exists());
    }

    @Test
    void exportToJson_WhenIOException_ShouldThrowRuntimeException() {
        // Передаём некорректный путь, чтобы спровоцировать ошибку
        ImportExportService faultyService = new ImportExportService(
                accountRepository, categoryRepository, operationRepository,
                jsonMapper, yamlMapper, "Z:\\несуществующий\\путь");
        assertThrows(RuntimeException.class, faultyService::exportToJson);
    }

    // --- YAML экспорт ---
    @Test
    void exportToYaml_ShouldCreateFiles() {
        when(accountRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(operationRepository.findAll()).thenReturn(List.of());

        importExportService.exportToYaml();

        File[] dirs = tempDir.toFile().listFiles((d, name) -> name.startsWith("yaml_"));
        assertNotNull(dirs);
        assertEquals(1, dirs.length);
        File exportDir = dirs[0];
        assertTrue(new File(exportDir, "accounts.yaml").exists());
        assertTrue(new File(exportDir, "categories.yaml").exists());
        assertTrue(new File(exportDir, "operations.yaml").exists());
    }

    // --- CSV экспорт ---
    @Test
    void exportToCsv_ShouldCreateFiles() {
        BankAccount acc = new BankAccount("Тест", "RUB");
        acc.setId("acc-1");
        Category cat = new Category("Категория", OperationType.INCOME, "");
        cat.setId("cat-1");
        Operation op = new Operation(OperationType.INCOME, acc, new BigDecimal("100"), "оп", cat);

        when(accountRepository.findAll()).thenReturn(List.of(acc));
        when(categoryRepository.findAll()).thenReturn(List.of(cat));
        when(operationRepository.findAll()).thenReturn(List.of(op));

        importExportService.exportToCsv();

        File[] dirs = tempDir.toFile().listFiles((d, name) -> name.startsWith("csv_"));
        assertNotNull(dirs);
        assertEquals(1, dirs.length);
        File exportDir = dirs[0];
        assertTrue(new File(exportDir, "accounts.csv").exists());
        assertTrue(new File(exportDir, "categories.csv").exists());
        assertTrue(new File(exportDir, "operations.csv").exists());
    }

    // --- Импорт из JSON ---
    @Test
    void importFromJson_ShouldReadFilesAndSaveToRepositories() throws IOException {
        // Подготавливаем тестовые данные
        BankAccount acc = new BankAccount("Тест", "RUB");
        Category cat = new Category("Категория", OperationType.INCOME, "");
        Operation op = new Operation(OperationType.INCOME, acc, new BigDecimal("100"), "оп", cat);

        // Создаём директорию с JSON файлами
        Path importDir = tempDir.resolve("import");
        Files.createDirectories(importDir);

        jsonMapper.writeValue(new File(importDir.toString(), "accounts.json"), new BankAccount[]{acc});
        jsonMapper.writeValue(new File(importDir.toString(), "categories.json"), new Category[]{cat});
        jsonMapper.writeValue(new File(importDir.toString(), "operations.json"), new Operation[]{op});

        // Мокаем поведение репозиториев
        when(accountRepository.save(any())).thenReturn(acc);
        when(categoryRepository.save(any())).thenReturn(cat);
        when(operationRepository.save(any())).thenReturn(op);

        // Выполняем импорт
        importExportService.importFromJson(importDir.toString());

        // Проверяем, что save вызывался нужное количество раз
        verify(accountRepository, times(1)).save(any());
        verify(categoryRepository, times(1)).save(any());
        verify(operationRepository, times(1)).save(any());
    }

    @Test
    void importFromJson_WhenDirectoryNotExists_ShouldThrow() {
        String badPath = tempDir.resolve("notexists").toString();
        assertThrows(IllegalArgumentException.class,
                () -> importExportService.importFromJson(badPath));
    }

    @Test
    void importFromJson_WhenAccountsFileMissing_ShouldSkipAndContinue() throws IOException {
        // Подготавливаем директорию с категориями и операциями, но без accounts.json
        Path importDir = tempDir.resolve("import");
        Files.createDirectories(importDir);

        Category cat = new Category("Тест", OperationType.INCOME, "");
        Operation op = new Operation(OperationType.INCOME, new BankAccount("Счет", "RUB"), BigDecimal.valueOf(100), "оп", cat);

        jsonMapper.writeValue(new File(importDir.toString(), "categories.json"), new Category[]{cat});
        jsonMapper.writeValue(new File(importDir.toString(), "operations.json"), new Operation[]{op});

        // Мокаем репозитории
        when(categoryRepository.save(any())).thenReturn(cat);
        when(operationRepository.save(any())).thenReturn(op);

        importExportService.importFromJson(importDir.toString());

        verify(categoryRepository, times(1)).save(any());
        verify(operationRepository, times(1)).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void importFromJson_WhenJsonIsMalformed_ShouldThrowRuntimeException() throws IOException {
        Path importDir = tempDir.resolve("import");
        Files.createDirectories(importDir);
        Files.writeString(importDir.resolve("accounts.json"), "{ malformed json }");

        assertThrows(RuntimeException.class, () -> importExportService.importFromJson(importDir.toString()));
    }

    @Test
    void exportToJson_WhenDirectoryCreationFails_ShouldThrowRuntimeException() throws IOException {
        Path tempFile = Files.createTempFile("test", ".tmp");
        String invalidPath = tempFile.toString(); // путь к файлу, а не директории

        ImportExportService faultyService = new ImportExportService(
                accountRepository, categoryRepository, operationRepository,
                jsonMapper, yamlMapper, invalidPath);

        assertThrows(RuntimeException.class, faultyService::exportToJson);
        Files.deleteIfExists(tempFile);
    }

}