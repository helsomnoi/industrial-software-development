package bank.factory.Impl;

import bank.domain.BankAccount;
import bank.factory.UniversalFactory;
import org.springframework.stereotype.Component;

@Component
public class BankAccountFactory implements UniversalFactory {

    @Override
    public BankAccount create(Object... params) {
        if (params.length < 2) {
            throw new IllegalArgumentException("Требуется имя и валюта");
        }
        String name = (String) params[0];
        String currency = (String) params[1];

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя счета не может быть пустым");
        }
        return new BankAccount(name, currency);
    }
}
