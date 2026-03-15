package bank.data_operations.exporter;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
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
class CsvDataExporterTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OperationRepository operationRepository;

    private CsvDataExporter csvExporter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        csvExporter = new CsvDataExporter();
        ReflectionTestUtils.setField(csvExporter, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(csvExporter, "categoryRepository", categoryRepository);
        ReflectionTestUtils.setField(csvExporter, "operationRepository", operationRepository);
        ReflectionTestUtils.setField(csvExporter, "exportPath", tempDir.toString());
    }

    @Test
    void exportData_ShouldCreateCsvFiles() {
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
        csvExporter.exportData();

        FilenameFilter csvFilter = (dir, name) -> name.startsWith("csv_");
        File[] dirs = tempDir.toFile().listFiles(csvFilter);
        assertTrue(dirs.length == 1);
        Path exportDir = dirs[0].toPath();
        assertTrue(Files.exists(exportDir.resolve("accounts.csv")));
        assertTrue(Files.exists(exportDir.resolve("categories.csv")));
        assertTrue(Files.exists(exportDir.resolve("operations.csv")));
    }

    @Test
    void exportData_WhenNoData_ShouldCreateEmptyCsvFiles() {
        when(accountRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(operationRepository.findAll()).thenReturn(List.of());

        csvExporter.exportData();

        FilenameFilter csvFilter = (dir, name) -> name.startsWith("csv_");
        File[] dirs = tempDir.toFile().listFiles(csvFilter);
        assertTrue(dirs.length == 1);
        Path exportDir = dirs[0].toPath();
        assertTrue(Files.exists(exportDir.resolve("accounts.csv")));
        assertTrue(Files.exists(exportDir.resolve("categories.csv")));
        assertTrue(Files.exists(exportDir.resolve("operations.csv")));
    }
}