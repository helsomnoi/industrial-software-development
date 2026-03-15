package bank.data_operations.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonDataImporter extends AbstractStructuredDataImporter {

    public JsonDataImporter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected String getAccountsFileName() {
        return "accounts.json";
    }

    @Override
    protected String getCategoriesFileName() {
        return "categories.json";
    }

    @Override
    protected String getOperationsFileName() {
        return "operations.json";
    }
}