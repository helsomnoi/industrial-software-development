package bank.data_operations.exporter;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YamlDataExporterTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OperationRepository operationRepository;

    private YAMLMapper yamlMapper;
    private YamlDataExporter yamlExporter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        yamlMapper = (YAMLMapper) new YAMLMapper()
                .registerModule(new JavaTimeModule());
        yamlExporter = new YamlDataExporter(yamlMapper);
        ReflectionTestUtils.setField(yamlExporter, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(yamlExporter, "categoryRepository", categoryRepository);
        ReflectionTestUtils.setField(yamlExporter, "operationRepository", operationRepository);
        ReflectionTestUtils.setField(yamlExporter, "exportPath", tempDir.toString());
    }

    @Test
    void exportData_ShouldCreateYamlFiles() {
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
        yamlExporter.exportData();


        FilenameFilter yamlFilter = (dir, name) -> name.startsWith("yaml_");
        File[] dirs = tempDir.toFile().listFiles(yamlFilter);
        assertEquals(1, dirs.length);
        Path exportDir = dirs[0].toPath();
        assertTrue(Files.exists(exportDir.resolve("accounts.yaml")));
        assertTrue(Files.exists(exportDir.resolve("categories.yaml")));
        assertTrue(Files.exists(exportDir.resolve("operations.yaml")));
    }

    @Test
    void exportData_WhenNoData_ShouldCreateEmptyFiles() {
        when(accountRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(operationRepository.findAll()).thenReturn(List.of());

        yamlExporter.exportData();

        FilenameFilter yamlFilter = (dir, name) -> name.startsWith("yaml_");
        File[] dirs = tempDir.toFile().listFiles(yamlFilter);
        assertEquals(1, dirs.length);
        Path exportDir = dirs[0].toPath();
        assertTrue(Files.exists(exportDir.resolve("accounts.yaml")));
        assertTrue(Files.exists(exportDir.resolve("categories.yaml")));
        assertTrue(Files.exists(exportDir.resolve("operations.yaml")));
    }
}