package bank.repository.inmemory;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOperationRepositoryTest {

    private InMemoryOperationRepository repository;
    private BankAccount account;
    private Category incomeCat;
    private Category expenseCat;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOperationRepository();
        account = new BankAccount("Тестовый счет", "RUB");
        // Устанавливаем ID, чтобы можно было сохранять
        account.setId("acc-1");
        incomeCat = new Category("Доход", OperationType.INCOME, "");
        incomeCat.setId("inc-1");
        expenseCat = new Category("Расход", OperationType.EXPENSE, "");
        expenseCat.setId("exp-1");
        now = LocalDateTime.now();
    }

    @Test
    void save_ShouldStoreOperation() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        Operation saved = repository.save(op);

        assertEquals(op, saved);
        Optional<Operation> found = repository.findById(op.getId());
        assertTrue(found.isPresent());
        assertEquals(op, found.get());
    }

    @Test
    void save_ShouldUpdateExistingOperation() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        repository.save(op);

        op.setAmount(new BigDecimal("200"));
        repository.save(op); // сохраняем снова

        Optional<Operation> found = repository.findById(op.getId());
        assertTrue(found.isPresent());
        assertEquals(0, new BigDecimal("200").compareTo(found.get().getAmount()));
    }

    @Test
    void findById_NotFound_ShouldReturnEmpty() {
        Optional<Operation> result = repository.findById("non-existent");
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllOperations() {
        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест1", incomeCat);
        Operation op2 = new Operation(OperationType.EXPENSE, account, new BigDecimal("200"), "тест2", expenseCat);
        repository.save(op1);
        repository.save(op2);

        List<Operation> all = repository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(op1));
        assertTrue(all.contains(op2));
    }

    @Test
    void deleteById_ShouldRemoveOperation() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        repository.save(op);
        repository.deleteById(op.getId());

        assertTrue(repository.findById(op.getId()).isEmpty());
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void findByAccountId_ShouldReturnOperationsForAccount() {
        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест1", incomeCat);
        Operation op2 = new Operation(OperationType.EXPENSE, account, new BigDecimal("200"), "тест2", expenseCat);
        Operation op3 = new Operation(OperationType.INCOME, account, new BigDecimal("300"), "тест3", incomeCat);
        repository.save(op1);
        repository.save(op2);
        repository.save(op3);

        List<Operation> result = repository.findByAccountId("acc-1");
        assertEquals(3, result.size());

        // Другой аккаунт
        List<Operation> resultOther = repository.findByAccountId("other");
        assertTrue(resultOther.isEmpty());
    }

    @Test
    void findByAccountIdAndDateBetween_ShouldReturnFilteredOperations() {
        LocalDateTime base = LocalDateTime.now();

        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест1", incomeCat);
        op1.setDate(base.minusDays(2));
        Operation op2 = new Operation(OperationType.INCOME, account, new BigDecimal("200"), "тест2", incomeCat);
        op2.setDate(base);
        Operation op3 = new Operation(OperationType.EXPENSE, account, new BigDecimal("300"), "тест3", expenseCat);
        op3.setDate(base.plusDays(2));

        repository.save(op1);
        repository.save(op2);
        repository.save(op3);

        LocalDateTime from = base.minusDays(1);
        LocalDateTime to = base.plusDays(1);
        List<Operation> result = repository.findByAccountIdAndDateBetween("acc-1", from, to);

        assertEquals(1, result.size());
        assertEquals(op2, result.get(0));
    }

    @Test
    void sumByAccountAndTypeAndDateBetween_ShouldReturnCorrectSum() {
        LocalDateTime base = LocalDateTime.now();

        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест1", incomeCat);
        op1.setDate(base.minusDays(2));
        Operation op2 = new Operation(OperationType.INCOME, account, new BigDecimal("200"), "тест2", incomeCat);
        op2.setDate(base);
        Operation op3 = new Operation(OperationType.INCOME, account, new BigDecimal("50"), "тест3", incomeCat);
        op3.setDate(base.plusDays(1));
        Operation op4 = new Operation(OperationType.EXPENSE, account, new BigDecimal("30"), "тест4", expenseCat);
        op4.setDate(base);

        repository.save(op1);
        repository.save(op2);
        repository.save(op3);
        repository.save(op4);

        LocalDateTime from = base.minusDays(1);
        LocalDateTime to = base.plusDays(2);
        BigDecimal sum = repository.sumByAccountAndTypeAndDateBetween("acc-1", OperationType.INCOME, from, to);

        assertEquals(0, new BigDecimal("250").compareTo(sum)); // 200 + 50
    }

    @Test
    void sumByAccountAndTypeAndDateBetween_NoMatches_ShouldReturnZero() {
        BigDecimal sum = repository.sumByAccountAndTypeAndDateBetween("acc-1", OperationType.INCOME,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        assertEquals(0, BigDecimal.ZERO.compareTo(sum));
    }

    @Test
    void groupByCategory_ShouldReturnCorrectGrouping() {
        LocalDateTime base = LocalDateTime.now();

        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест1", incomeCat);
        op1.setDate(base.minusDays(1).plusHours(1)); // чуть позже from, чтобы попасть
        Operation op2 = new Operation(OperationType.INCOME, account, new BigDecimal("200"), "тест2", incomeCat);
        op2.setDate(base);
        Operation op3 = new Operation(OperationType.INCOME, account, new BigDecimal("50"), "тест3", incomeCat);
        op3.setDate(base.plusDays(1).minusHours(1)); // чуть раньше to
        Operation op4 = new Operation(OperationType.EXPENSE, account, new BigDecimal("30"), "тест4", expenseCat);
        op4.setDate(base);

        repository.save(op1);
        repository.save(op2);
        repository.save(op3);
        repository.save(op4);

        LocalDateTime from = base.minusDays(1);
        LocalDateTime to = base.plusDays(1);

        // Группировка по доходу
        List<Object[]> incomeGroups = repository.groupByCategory("acc-1", OperationType.INCOME, from, to);
        assertEquals(1, incomeGroups.size());
        Object[] entry = incomeGroups.get(0);
        assertEquals("inc-1", entry[0]);
        BigDecimal expectedSum = new BigDecimal("350"); // 100+200+50
        assertEquals(0, expectedSum.compareTo((BigDecimal) entry[1]));

        // Группировка по расходу
        List<Object[]> expenseGroups = repository.groupByCategory("acc-1", OperationType.EXPENSE, from, to);
        assertEquals(1, expenseGroups.size());
        Object[] expEntry = expenseGroups.get(0);
        assertEquals("exp-1", expEntry[0]);
        assertEquals(0, new BigDecimal("30").compareTo((BigDecimal) expEntry[1]));
    }

    @Test
    void groupByCategory_WhenNoCategory_ShouldBeExcluded() {
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "без категории", null);
        op.setDate(now);
        repository.save(op);

        List<Object[]> groups = repository.groupByCategory("acc-1", OperationType.INCOME, now.minusDays(1), now.plusDays(1));
        assertTrue(groups.isEmpty()); // операция без категории не должна попасть в группировку
    }

    @Test
    void groupByCategory_WhenMultipleCategories_ShouldGroupCorrectly() {
        Category incomeCat2 = new Category("Доход2", OperationType.INCOME, "");
        incomeCat2.setId("inc-2");

        Operation op1 = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест1", incomeCat);
        op1.setDate(now);
        Operation op2 = new Operation(OperationType.INCOME, account, new BigDecimal("200"), "тест2", incomeCat2);
        op2.setDate(now);
        repository.save(op1);
        repository.save(op2);

        List<Object[]> groups = repository.groupByCategory("acc-1", OperationType.INCOME, now.minusDays(1), now.plusDays(1));
        assertEquals(2, groups.size());

        // Преобразуем в карту для удобства
        var map = groups.stream().collect(java.util.stream.Collectors.toMap(
                e -> (String) e[0],
                e -> (BigDecimal) e[1]
        ));
        assertEquals(0, new BigDecimal("100").compareTo(map.get("inc-1")));
        assertEquals(0, new BigDecimal("200").compareTo(map.get("inc-2")));
    }

    @Test
    void groupByCategory_WhenNoOperationsInPeriod_ShouldReturnEmpty() {
        LocalDateTime now = LocalDateTime.now();
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        op.setDate(now.minusDays(5));
        repository.save(op);

        List<Object[]> result = repository.groupByCategory("acc-1", OperationType.INCOME,
                now.minusDays(1), now.plusDays(1));
        assertTrue(result.isEmpty());
    }

    @Test
    void findByAccountIdAndDateBetween_WhenNoMatches_ShouldReturnEmpty() {
        LocalDateTime now = LocalDateTime.now();
        List<Operation> result = repository.findByAccountIdAndDateBetween("acc-1", now.minusDays(1), now.plusDays(1));
        assertTrue(result.isEmpty());
    }

    @Test
    void findByAccountIdAndDateBetween_WhenDateEqualsFrom_ShouldExclude() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(1);
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        op.setDate(from);
        repository.save(op);

        List<Operation> result = repository.findByAccountIdAndDateBetween(account.getId(), from, to);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByAccountIdAndDateBetween_WhenDateEqualsTo_ShouldExclude() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(1);
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        op.setDate(to);
        repository.save(op);

        List<Operation> result = repository.findByAccountIdAndDateBetween(account.getId(), from, to);
        assertTrue(result.isEmpty());
    }

    @Test
    void sumByAccountAndTypeAndDateBetween_WhenDateEqualsFrom_ShouldReturnZero() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(1);
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        op.setDate(from);
        repository.save(op);

        BigDecimal sum = repository.sumByAccountAndTypeAndDateBetween(account.getId(), OperationType.INCOME, from, to);
        assertEquals(0, sum.compareTo(BigDecimal.ZERO));
    }

    @Test
    void sumByAccountAndTypeAndDateBetween_WhenDateEqualsTo_ShouldReturnZero() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(1);
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        op.setDate(to);
        repository.save(op);

        BigDecimal sum = repository.sumByAccountAndTypeAndDateBetween(account.getId(), OperationType.INCOME, from, to);
        assertEquals(0, sum.compareTo(BigDecimal.ZERO));
    }

    @Test
    void groupByCategory_WhenDateEqualsFrom_ShouldExclude() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(1);
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        op.setDate(from);
        repository.save(op);

        List<Object[]> result = repository.groupByCategory(account.getId(), OperationType.INCOME, from, to);
        assertTrue(result.isEmpty());
    }

    @Test
    void groupByCategory_WhenDateEqualsTo_ShouldExclude() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(1);
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", incomeCat);
        op.setDate(to);
        repository.save(op);

        List<Object[]> result = repository.groupByCategory(account.getId(), OperationType.INCOME, from, to);
        assertTrue(result.isEmpty());
    }
}