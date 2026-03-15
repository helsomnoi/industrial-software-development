package bank.data_operations.importer;

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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YamlDataImporterTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private OperationRepository operationRepository;

    private YAMLMapper yamlMapper;
    private YamlDataImporter yamlDataImporter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        yamlMapper = (YAMLMapper) new YAMLMapper()
                .registerModule(new JavaTimeModule());
        yamlDataImporter = new YamlDataImporter(yamlMapper);
        ReflectionTestUtils.setField(yamlDataImporter, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(yamlDataImporter, "categoryRepository", categoryRepository);
        ReflectionTestUtils.setField(yamlDataImporter, "operationRepository", operationRepository);
    }

    @Test
    void importData_shouldSaveOperations_whenAccountsAndCategoriesAlreadyExist() throws IOException {
        BankAccount account = new BankAccount("Existing Account", "RUB");
        account.setId("acc-1");
        Category category = new Category("Existing Category", OperationType.INCOME, "desc");
        category.setId("cat-1");
        Operation operation = new Operation(OperationType.INCOME, account, BigDecimal.valueOf(500), "test", category);
        operation.setId("op-1");

        yamlMapper.writeValue(tempDir.resolve("accounts.yaml").toFile(), new BankAccount[]{account});
        yamlMapper.writeValue(tempDir.resolve("categories.yaml").toFile(), new Category[]{category});
        yamlMapper.writeValue(tempDir.resolve("operations.yaml").toFile(), new Operation[]{operation});

        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(category));
        when(operationRepository.findById("op-1")).thenReturn(Optional.empty());

        yamlDataImporter.importData(tempDir.toString());

        verify(accountRepository, never()).save(any());
        verify(categoryRepository, never()).save(any());
        verify(operationRepository).save(argThat(o -> "op-1".equals(o.getId())));
    }

    @Test
    void importData_shouldSkipOperationAndSaveAccountAndCategory_whenAccountNotFound() throws IOException {
        BankAccount account = new BankAccount("New Account", "RUB");
        account.setId("acc-1");
        Category category = new Category("New Category", OperationType.INCOME, "desc");
        category.setId("cat-1");
        Operation operation = new Operation(OperationType.INCOME, account, BigDecimal.valueOf(500), "test", category);
        operation.setId("op-1");

        yamlMapper.writeValue(tempDir.resolve("accounts.yaml").toFile(), new BankAccount[]{account});
        yamlMapper.writeValue(tempDir.resolve("categories.yaml").toFile(), new Category[]{category});
        yamlMapper.writeValue(tempDir.resolve("operations.yaml").toFile(), new Operation[]{operation});

        yamlDataImporter.importData(tempDir.toString());

        verify(accountRepository).save(argThat(a -> "acc-1".equals(a.getId())));
        verify(categoryRepository).save(argThat(c -> "cat-1".equals(c.getId())));
        verify(operationRepository, never()).save(any());
    }

    @Test
    void importData_shouldSaveOperationWithNullCategory_whenCategoryNotFound() throws IOException {
        BankAccount account = new BankAccount("Existing Account", "RUB");
        account.setId("acc-1");
        Category category = new Category("New Category", OperationType.INCOME, "desc");
        category.setId("cat-1");
        Operation operation = new Operation(OperationType.INCOME, account, BigDecimal.valueOf(500), "test", category);
        operation.setId("op-1");

        yamlMapper.writeValue(tempDir.resolve("accounts.yaml").toFile(), new BankAccount[]{account});
        yamlMapper.writeValue(tempDir.resolve("categories.yaml").toFile(), new Category[]{category});
        yamlMapper.writeValue(tempDir.resolve("operations.yaml").toFile(), new Operation[]{operation});

        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        when(operationRepository.findById("op-1")).thenReturn(Optional.empty());

        yamlDataImporter.importData(tempDir.toString());

        verify(accountRepository, never()).save(any());
        verify(categoryRepository).save(argThat(c -> "cat-1".equals(c.getId())));
        verify(operationRepository).save(argThat(o -> "op-1".equals(o.getId()) && o.getCategory() == null));
    }

    @Test
    void importData_shouldNotSaveDuplicateEntities() throws IOException {
        BankAccount account = new BankAccount("Existing Account", "RUB");
        account.setId("acc-1");
        yamlMapper.writeValue(tempDir.resolve("accounts.yaml").toFile(), new BankAccount[]{account});

        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));

        yamlDataImporter.importData(tempDir.toString());

        verify(accountRepository, never()).save(any());
    }

    @Test
    void importData_shouldHandleMissingFilesGracefully() throws IOException {
        BankAccount account = new BankAccount("New Account", "RUB");
        account.setId("acc-1");
        yamlMapper.writeValue(tempDir.resolve("accounts.yaml").toFile(), new BankAccount[]{account});

        yamlDataImporter.importData(tempDir.toString());

        verify(accountRepository).save(any(BankAccount.class));
        verify(categoryRepository, never()).save(any());
        verify(operationRepository, never()).save(any());
    }

    @Test
    void importData_shouldThrowException_whenYamlMalformed() throws IOException {
        Files.writeString(tempDir.resolve("accounts.yaml"), "{ invalid yaml }");
        assertThrows(RuntimeException.class, () -> yamlDataImporter.importData(tempDir.toString()));
    }
}