package bank.repository.inmemory;

import bank.domain.BankAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryAccountRepositoryTest {

    private InMemoryAccountRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
    }

    @Test
    void save_ShouldStoreAccount() {
        BankAccount account = new BankAccount("Счет", "RUB");
        BankAccount saved = repository.save(account);
        assertEquals(account, saved);
        assertTrue(repository.findById(account.getId()).isPresent());
    }

    @Test
    void findById_ShouldReturnEmptyIfNotFound() {
        Optional<BankAccount> result = repository.findById("non-existent");
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllAccounts() {
        BankAccount acc1 = repository.save(new BankAccount("Счет1", "RUB"));
        BankAccount acc2 = repository.save(new BankAccount("Счет2", "RUB"));
        List<BankAccount> all = repository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(acc1));
        assertTrue(all.contains(acc2));
    }

    @Test
    void deleteById_ShouldRemoveAccount() {
        BankAccount account = repository.save(new BankAccount("Счет", "RUB"));
        repository.deleteById(account.getId());
        assertTrue(repository.findById(account.getId()).isEmpty());
    }

    @Test
    void existsByName_ShouldReturnTrueIfExists() {
        repository.save(new BankAccount("Уникальный", "RUB"));
        assertTrue(repository.existsByName("Уникальный"));
        assertFalse(repository.existsByName("Другой"));
    }
}