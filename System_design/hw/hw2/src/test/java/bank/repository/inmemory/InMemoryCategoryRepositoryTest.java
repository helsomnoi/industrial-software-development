package bank.repository.inmemory;

import bank.domain.Category;
import bank.domain.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryCategoryRepositoryTest {
    private InMemoryCategoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCategoryRepository();
    }

    @Test
    void save_ShouldStoreCategory() {
        Category cat = new Category("Зарплата", OperationType.INCOME, "");
        Category saved = repository.save(cat);
        assertEquals(cat, saved);
        assertTrue(repository.findById(cat.getId()).isPresent());
    }

    @Test
    void findByType_ShouldReturnFiltered() {
        repository.save(new Category("Зарплата", OperationType.INCOME, ""));
        repository.save(new Category("Еда", OperationType.EXPENSE, ""));
        repository.save(new Category("Кэшбэк", OperationType.INCOME, ""));

        List<Category> income = repository.findByType(OperationType.INCOME);
        assertEquals(2, income.size());
        List<Category> expense = repository.findByType(OperationType.EXPENSE);
        assertEquals(1, expense.size());
    }

    @Test
    void existsByNameAndType_ShouldWork() {
        repository.save(new Category("Зарплата", OperationType.INCOME, ""));
        assertTrue(repository.existsByNameAndType("Зарплата", OperationType.INCOME));
        assertFalse(repository.existsByNameAndType("Зарплата", OperationType.EXPENSE));
        assertFalse(repository.existsByNameAndType("Еда", OperationType.INCOME));
    }

    @Test
    void findById_ExistingId_ShouldReturnCategory() {
        Category cat = new Category("Тест", OperationType.INCOME, "описание");
        repository.save(cat);
        Optional<Category> found = repository.findById(cat.getId());
        assertTrue(found.isPresent());
        assertEquals(cat, found.get());
    }

    @Test
    void deleteById_ShouldRemoveCategory() {
        Category cat = new Category("Тест", OperationType.INCOME, "описание");
        repository.save(cat);
        repository.deleteById(cat.getId());
        assertTrue(repository.findById(cat.getId()).isEmpty());
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void deleteById_NonExisting_ShouldDoNothing() {
        repository.deleteById("non-existent");
        // не должно быть исключений
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void findByType_WhenNoCategories_ShouldReturnEmptyList() {
        List<Category> result = repository.findByType(OperationType.INCOME);
        assertTrue(result.isEmpty());
    }
}
