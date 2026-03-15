package bank.data_operations.exporter;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;

@Component
public class CsvDataExporter extends DataExporter {

    @Override
    protected String getFormat() {
        return "csv";
    }

    @Override
    protected void writeAccounts(Path dir, List<bank.domain.BankAccount> accounts) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(dir + "/accounts.csv"))) {
            writer.writeNext(new String[]{"ID", "Name", "Balance", "Currency", "CreatedAt"});
            accounts.forEach(acc -> writer.writeNext(new String[]{
                    acc.getId(),
                    acc.getName(),
                    acc.getBalance().toString(),
                    acc.getCurrency(),
                    acc.getCreatedAt().toString()
            }));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи accounts.csv", e);
        }
    }

    @Override
    protected void writeCategories(Path dir, List<bank.domain.Category> categories) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(dir + "/categories.csv"))) {
            writer.writeNext(new String[]{"ID", "Name", "Type", "Description", "CreatedAt"});
            categories.forEach(cat -> writer.writeNext(new String[]{
                    cat.getId(),
                    cat.getName(),
                    cat.getType().toString(),
                    cat.getDescription(),
                    cat.getCreatedAt().toString()
            }));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи categories.csv", e);
        }
    }

    @Override
    protected void writeOperations(Path dir, List<bank.domain.Operation> operations) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(dir + "/operations.csv"))) {
            writer.writeNext(new String[]{"ID", "Type", "AccountID", "Amount", "Date", "Description", "CategoryID"});
            operations.forEach(op -> writer.writeNext(new String[]{
                    op.getId(),
                    op.getType().toString(),
                    op.getBankAccountId(),
                    op.getAmount().toString(),
                    op.getDate().toString(),
                    op.getDescription(),
                    op.getCategoryId() != null ? op.getCategoryId() : ""
            }));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи operations.csv", e);
        }
    }
}