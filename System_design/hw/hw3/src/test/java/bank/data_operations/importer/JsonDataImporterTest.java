package bank.data_operations.importer;

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
class JsonDataImporterTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private OperationRepository operationRepository;

    private ObjectMapper objectMapper;
    private JsonDataImporter jsonDataImporter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonDataImporter = new JsonDataImporter(objectMapper);
        ReflectionTestUtils.setField(jsonDataImporter, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(jsonDataImporter, "categoryRepository", categoryRepository);
        ReflectionTestUtils.setField(jsonDataImporter, "operationRepository", operationRepository);
    }

    @Test
    void importData_shouldSaveOperations_whenAccountsAndCategoriesAlreadyExist() throws IOException {
        BankAccount account = new BankAccount("Existing Account", "RUB");
        account.setId("acc-1");
        Category category = new Category("Existing Category", OperationType.INCOME, "desc");
        category.setId("cat-1");
        Operation operation = new Operation(OperationType.INCOME, account, BigDecimal.valueOf(500), "test", category);
        operation.setId("op-1");

        objectMapper.writeValue(tempDir.resolve("accounts.json").toFile(), new BankAccount[]{account});
        objectMapper.writeValue(tempDir.resolve("categories.json").toFile(), new Category[]{category});
        objectMapper.writeValue(tempDir.resolve("operations.json").toFile(), new Operation[]{operation});

        // Счёт и категория уже есть в БД
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(category));
        // Операция новая
        when(operationRepository.findById("op-1")).thenReturn(Optional.empty());

        jsonDataImporter.importData(tempDir.toString());

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

        objectMapper.writeValue(tempDir.resolve("accounts.json").toFile(), new BankAccount[]{account});
        objectMapper.writeValue(tempDir.resolve("categories.json").toFile(), new Category[]{category});
        objectMapper.writeValue(tempDir.resolve("operations.json").toFile(), new Operation[]{operation});

        // Не настраиваем репозитории — по умолчанию findById вернёт Optional.empty(),
        // что означает, что все объекты новые и будут сохранены.
        // Операция не сохранится, потому что в parseOperations счёт не найден (дефолт empty).

        jsonDataImporter.importData(tempDir.toString());

        // Счёт и категория должны сохраниться (так как они новые)
        verify(accountRepository).save(argThat(a -> "acc-1".equals(a.getId())));
        verify(categoryRepository).save(argThat(c -> "cat-1".equals(c.getId())));
        // Операция не должна сохраниться
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

        objectMapper.writeValue(tempDir.resolve("accounts.json").toFile(), new BankAccount[]{account});
        objectMapper.writeValue(tempDir.resolve("categories.json").toFile(), new Category[]{category});
        objectMapper.writeValue(tempDir.resolve("operations.json").toFile(), new Operation[]{operation});

        // Счёт уже есть в БД
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        // Категорию не настраиваем — по умолчанию empty, значит она не найдена
        // Операция новая
        when(operationRepository.findById("op-1")).thenReturn(Optional.empty());

        jsonDataImporter.importData(tempDir.toString());

        // Счёт не сохраняется (уже есть)
        verify(accountRepository, never()).save(any());
        // Категория сохраняется (новая)
        verify(categoryRepository).save(argThat(c -> "cat-1".equals(c.getId())));
        // Операция сохраняется, но с category = null
        verify(operationRepository).save(argThat(o -> "op-1".equals(o.getId()) && o.getCategory() == null));
    }

    @Test
    void importData_shouldNotSaveDuplicateEntities() throws IOException {
        BankAccount account = new BankAccount("Existing Account", "RUB");
        account.setId("acc-1");
        objectMapper.writeValue(tempDir.resolve("accounts.json").toFile(), new BankAccount[]{account});

        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));

        jsonDataImporter.importData(tempDir.toString());

        verify(accountRepository, never()).save(any());
    }

    @Test
    void importData_shouldHandleMissingFilesGracefully() throws IOException {
        BankAccount account = new BankAccount("New Account", "RUB");
        account.setId("acc-1");
        objectMapper.writeValue(tempDir.resolve("accounts.json").toFile(), new BankAccount[]{account});

        jsonDataImporter.importData(tempDir.toString());

        verify(accountRepository).save(any(BankAccount.class));
        verify(categoryRepository, never()).save(any());
        verify(operationRepository, never()).save(any());
    }

    @Test
    void importData_shouldThrowException_whenJsonMalformed() throws IOException {
        Files.writeString(tempDir.resolve("accounts.json"), "{ invalid json }");
        assertThrows(RuntimeException.class, () -> jsonDataImporter.importData(tempDir.toString()));
    }
}