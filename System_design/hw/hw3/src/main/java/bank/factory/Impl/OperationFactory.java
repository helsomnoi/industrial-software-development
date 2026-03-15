package bank.factory.Impl;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.factory.UniversalFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OperationFactory implements UniversalFactory {
    @Override
    public Operation create(Object... params) {
        if (params.length < 5) {
            throw new IllegalArgumentException("Требуется тип, счет, сумма, описание и категория");
        }
        OperationType type = (OperationType) params[0];
        BankAccount account = (BankAccount) params[1];
        BigDecimal amount = (BigDecimal) params[2];
        String description = (String) params[3];
        Category category = (Category) params[4];

        if (category != null && category.getType() != type) {
            throw new IllegalArgumentException(
                    String.format("Тип операции (%s) не соответствует типу категории (%s)",
                            type, category.getType()));
        }

        return new Operation(type, account, amount, description, category);
    }
}
