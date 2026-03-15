package bank.data_operations.importer;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.stereotype.Component;

@Component
public class YamlDataImporter extends AbstractStructuredDataImporter {

    public YamlDataImporter(YAMLMapper yamlMapper) {
        super(yamlMapper);
    }

    @Override
    protected String getAccountsFileName() {
        return "accounts.yaml";
    }

    @Override
    protected String getCategoriesFileName() {
        return "categories.yaml";
    }

    @Override
    protected String getOperationsFileName() {
        return "operations.yaml";
    }
}