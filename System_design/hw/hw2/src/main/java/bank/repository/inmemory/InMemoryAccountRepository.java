package bank.repository.inmemory;

import bank.domain.BankAccount;
import bank.repository.AccountRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, BankAccount> storage = new ConcurrentHashMap<>();

    @Override
    public BankAccount save(BankAccount account) {
        storage.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<BankAccount> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }

    @Override
    public boolean existsByName(String name) {
        return storage.values().stream().anyMatch(a -> a.getName().equals(name));
    }
}