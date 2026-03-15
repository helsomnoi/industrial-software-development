package bank.data_operations.exporter;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class YamlDataExporter extends DataExporter {

    private final YAMLMapper yamlMapper;

    public YamlDataExporter(YAMLMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    @Override
    protected String getFormat() {
        return "yaml";
    }

    @Override
    protected void writeAccounts(Path dir, List<bank.domain.BankAccount> accounts) {
        try {
            yamlMapper.writeValue(new File(dir.toString(), "accounts.yaml"), accounts);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи accounts.yaml", e);
        }
    }

    @Override
    protected void writeCategories(Path dir, List<bank.domain.Category> categories) {
        try {
            yamlMapper.writeValue(new File(dir.toString(), "categories.yaml"), categories);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи categories.yaml", e);
        }
    }

    @Override
    protected void writeOperations(Path dir, List<bank.domain.Operation> operations) {
        try {
            yamlMapper.writeValue(new File(dir.toString(), "operations.yaml"), operations);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи operations.yaml", e);
        }
    }
}