package bank.data_operations.exporter;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonDataExporterTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OperationRepository operationRepository;

    private ObjectMapper objectMapper;
    private JsonDataExporter jsonExporter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
        jsonExporter = new JsonDataExporter(objectMapper);
        ReflectionTestUtils.setField(jsonExporter, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(jsonExporter, "categoryRepository", categoryRepository);
        ReflectionTestUtils.setField(jsonExporter, "operationRepository", operationRepository);
        ReflectionTestUtils.setField(jsonExporter, "exportPath", tempDir.toString());
    }

    @Test
    void exportData_ShouldCreateJsonFiles() {
        // Подготовка тестовых данных
        BankAccount acc = new BankAccount("Тест", "RUB");
        acc.setId("acc-1");
        Category cat = new Category("Кат", OperationType.INCOME, "описание");
        cat.setId("cat-1");
        Operation op = new Operation(OperationType.INCOME, acc, new BigDecimal("100"), "тест", cat);
        op.setId("op-1");

        when(accountRepository.findAll()).thenReturn(List.of(acc));
        when(categoryRepository.findAll()).thenReturn(List.of(cat));
        when(operationRepository.findAll()).thenReturn(List.of(op));

        // Экспорт
        jsonExporter.exportData();

        // Проверка создания файлов
        FilenameFilter jsonFilter = (dir, name) -> name.startsWith("json_");
        File[] dirs = tempDir.toFile().listFiles(jsonFilter);
        assertTrue(dirs.length == 1);
        Path exportDir = dirs[0].toPath();
        assertTrue(Files.exists(exportDir.resolve("accounts.json")));
        assertTrue(Files.exists(exportDir.resolve("categories.json")));
        assertTrue(Files.exists(exportDir.resolve("operations.json")));
    }

    @Test
    void exportData_WhenNoData_ShouldCreateEmptyFiles() {
        when(accountRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(operationRepository.findAll()).thenReturn(List.of());

        jsonExporter.exportData();

        FilenameFilter jsonFilter = (dir, name) -> name.startsWith("json_");
        File[] dirs = tempDir.toFile().listFiles(jsonFilter);
        assertTrue(dirs.length == 1);
        Path exportDir = dirs[0].toPath();
        assertTrue(Files.exists(exportDir.resolve("accounts.json")));
        assertTrue(Files.exists(exportDir.resolve("categories.json")));
        assertTrue(Files.exists(exportDir.resolve("operations.json")));
    }
}