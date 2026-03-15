package bank.factory.Impl;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.OperationType;
import bank.factory.UniversalFactory;
import org.springframework.stereotype.Component;

@Component
public class CategoryFactory implements UniversalFactory {
    @Override
    public Category create(Object... params) {
        if (params.length < 3) {
            throw new IllegalArgumentException("Требуется имя, тип и описание");
        }
        String name = (String) params[0];
        OperationType type = (OperationType) params[1];
        String description = (String) params[2];

        return new Category(name, type, description);
    }
}
