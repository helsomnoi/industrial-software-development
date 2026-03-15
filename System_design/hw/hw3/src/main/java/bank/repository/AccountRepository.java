package bank.repository;

import bank.domain.BankAccount;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findById(String id);
    List<BankAccount> findAll();
    void deleteById(String id);
    boolean existsByName(String name);
}