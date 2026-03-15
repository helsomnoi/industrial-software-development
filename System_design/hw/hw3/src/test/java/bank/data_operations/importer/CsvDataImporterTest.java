package bank.data_operations.importer;

import bank.domain.BankAccount;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import com.opencsv.CSVWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvDataImporterTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OperationRepository operationRepository;

    private CsvDataImporter csvDataImporter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        csvDataImporter = new CsvDataImporter();
        ReflectionTestUtils.setField(csvDataImporter, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(csvDataImporter, "categoryRepository", categoryRepository);
        ReflectionTestUtils.setField(csvDataImporter, "operationRepository", operationRepository);
    }

    @Test
    void importData_WithAccountsCsv_ShouldSaveAccounts() throws Exception {
        Path accountsFile = tempDir.resolve("accounts.csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(accountsFile.toFile()))) {
            writer.writeNext(new String[]{"ID", "Name", "Balance", "Currency", "CreatedAt"});
            writer.writeNext(new String[]{"acc-1", "Счет", "100.00", "RUB", "2025-01-01T10:00:00"});
        }

        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        csvDataImporter.importData(tempDir.toString());

        verify(accountRepository).save(any(BankAccount.class));
        verifyNoInteractions(categoryRepository, operationRepository);
    }
}