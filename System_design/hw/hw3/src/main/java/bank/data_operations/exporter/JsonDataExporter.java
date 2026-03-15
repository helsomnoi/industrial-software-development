package bank.data_operations.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class JsonDataExporter extends DataExporter {

    private final ObjectMapper objectMapper;

    public JsonDataExporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected String getFormat() {
        return "json";
    }

    @Override
    protected void writeAccounts(Path dir, List<bank.domain.BankAccount> accounts) {
        try {
            objectMapper.writeValue(new File(dir.toString(), "accounts.json"), accounts);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи accounts.json", e);
        }
    }

    @Override
    protected void writeCategories(Path dir, List<bank.domain.Category> categories) {
        try {
            objectMapper.writeValue(new File(dir.toString(), "categories.json"), categories);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи categories.json", e);
        }
    }

    @Override
    protected void writeOperations(Path dir, List<bank.domain.Operation> operations) {
        try {
            objectMapper.writeValue(new File(dir.toString(), "operations.json"), operations);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи operations.json", e);
        }
    }
}