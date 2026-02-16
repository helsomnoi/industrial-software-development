package bank.service;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private BankService bankService;

    private BankAccount testAccount;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testAccount = new BankAccount("Тестовый счет", "RUB");
        testAccount.setId("acc-1");
        testCategory = new Category("Тестовая категория", OperationType.INCOME, "описание");
        testCategory.setId("cat-1");
    }

    // --- Account tests ---
    @Test
    void createAccount_Success() {
        when(accountRepository.existsByName("Основной")).thenReturn(false);
        when(accountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        BankAccount account = bankService.createAccount("Основной", "RUB");

        assertNotNull(account);
        assertEquals("Основной", account.getName());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals("RUB", account.getCurrency());
        verify(accountRepository).save(account);
    }

    @Test
    void createAccount_DuplicateName_ThrowsException() {
        when(accountRepository.existsByName("Основной")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bankService.createAccount("Основной", "RUB"));
        assertEquals("Счет с именем 'Основной' уже существует", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_Success() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        BankAccount updated = bankService.updateAccount("acc-1", "Новое имя");

        assertEquals("Новое имя", updated.getName());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void deleteAccount_Success() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(operationRepository.findByAccountId("acc-1")).thenReturn(List.of());

        bankService.deleteAccount("acc-1");

        verify(accountRepository).deleteById("acc-1");
    }

    @Test
    void getAccount_NotFound_ThrowsException() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bankService.getAccount("acc-1"));
    }

    @Test
    void getAllAccounts_ShouldReturnList() {
        when(accountRepository.findAll()).thenReturn(List.of(testAccount));
        List<BankAccount> accounts = bankService.getAllAccounts();
        assertEquals(1, accounts.size());
        assertEquals(testAccount, accounts.get(0));
    }

    // --- Category tests ---
    @Test
    void createCategory_Success() {
        when(categoryRepository.existsByNameAndType("Зарплата", OperationType.INCOME)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category category = bankService.createCategory("Зарплата", OperationType.INCOME, "Доход");

        assertNotNull(category);
        assertEquals("Зарплата", category.getName());
        assertEquals(OperationType.INCOME, category.getType());
        verify(categoryRepository).save(category);
    }

    @Test
    void createCategory_Duplicate_ThrowsException() {
        when(categoryRepository.existsByNameAndType("Зарплата", OperationType.INCOME)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> bankService.createCategory("Зарплата", OperationType.INCOME, ""));
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category updated = bankService.updateCategory("cat-1", "Новое имя", "Новое описание");

        assertEquals("Новое имя", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void deleteCategory_WithoutOperations_Success() {
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(testCategory));
        when(operationRepository.findByCategoryId("cat-1")).thenReturn(List.of());
        doNothing().when(categoryRepository).deleteById("cat-1");

        bankService.deleteCategory("cat-1");
        verify(categoryRepository).deleteById("cat-1");
    }

    @Test
    void deleteCategory_WithOperations_ThrowsException() {
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(testCategory));
        when(operationRepository.findByCategoryId("cat-1")).thenReturn(List.of(mock(Operation.class)));

        assertThrows(IllegalStateException.class, () -> bankService.deleteCategory("cat-1"));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        List<Category> categories = bankService.getAllCategories();
        assertEquals(1, categories.size());
    }

    @Test
    void getCategoriesByType_ShouldReturnFiltered() {
        when(categoryRepository.findByType(OperationType.INCOME)).thenReturn(List.of(testCategory));
        List<Category> result = bankService.getCategoriesByType(OperationType.INCOME);
        assertEquals(1, result.size());
        assertEquals(testCategory, result.get(0));
    }

    // --- Operation tests ---
    @Test
    void createOperation_Income_Success() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(testCategory));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(operationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Operation op = bankService.createOperation(OperationType.INCOME, "acc-1",
                new BigDecimal("1000.00"), "описание", "cat-1");

        assertNotNull(op);
        assertEquals(0, new BigDecimal("1000.00").compareTo(testAccount.getBalance()));
        verify(operationRepository).save(op);
    }

    @Test
    void createOperation_TypeMismatch_ThrowsException() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(testCategory));

        assertThrows(IllegalArgumentException.class,
                () -> bankService.createOperation(OperationType.EXPENSE, "acc-1",
                        new BigDecimal("100.00"), "тест", "cat-1"));
    }

    @Test
    void createOperation_WithoutCategory_Success() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(operationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Operation op = bankService.createOperation(OperationType.INCOME, "acc-1",
                new BigDecimal("777.00"), "без категории", null);

        assertNull(op.getCategoryId());
        verify(operationRepository).save(op);
    }

    @Test
    void getAccountOperations_ShouldReturnList() {
        LocalDateTime now = LocalDateTime.now();
        when(operationRepository.findByAccountIdAndDateBetween(eq("acc-1"), any(), any()))
                .thenReturn(List.of(mock(Operation.class)));

        List<Operation> ops = bankService.getAccountOperations("acc-1", now.minusDays(1), now.plusDays(1));
        assertEquals(1, ops.size());
    }

    // --- Balance tests ---
    @Test
    void recalculateAccountBalance_Success() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bankService.recalculateAccountBalance("acc-1");
        verify(accountRepository).save(testAccount);
    }

    @Test
    void recalculateAllBalances_Success() {
        BankAccount acc1 = mock(BankAccount.class);
        BankAccount acc2 = mock(BankAccount.class);
        when(accountRepository.findAll()).thenReturn(List.of(acc1, acc2));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bankService.recalculateAllBalances();
        verify(acc1).recalculateBalance();
        verify(acc2).recalculateBalance();
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    void checkAllBalances_ShouldNotThrow() {
        when(accountRepository.findAll()).thenReturn(List.of(testAccount));
        when(operationRepository.sumByAccountAndTypeAndDateBetween(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        assertDoesNotThrow(() -> bankService.checkAllBalances());
    }

    @Test
    void deleteAccount_WithOperations_ShouldDeleteOperations() {
        BankAccount account = new BankAccount("Счет", "RUB");
        account.setId("acc-1");
        Operation op = new Operation(OperationType.INCOME, account, new BigDecimal("100"), "тест", null);
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        when(operationRepository.findByAccountId("acc-1")).thenReturn(List.of(op));
        doNothing().when(operationRepository).deleteById(op.getId());
        doNothing().when(accountRepository).deleteById("acc-1");

        bankService.deleteAccount("acc-1");

        verify(operationRepository).deleteById(op.getId());
        verify(accountRepository).deleteById("acc-1");
    }

    @Test
    void createOperation_WhenCategoryNotFound_ShouldThrow() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bankService.createOperation(OperationType.INCOME, "acc-1",
                        new BigDecimal("100"), "тест", "cat-1"));
    }

    @Test
    void createOperation_WhenAccountNotFound_ShouldThrow() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> bankService.createOperation(OperationType.INCOME, "acc-1",
                        new BigDecimal("100"), "тест", null));
    }

    @Test
    void updateAccount_WhenAccountNotFound_ShouldThrow() {
        when(accountRepository.findById("non-existent")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> bankService.updateAccount("non-existent", "new name"));
    }

    @Test
    void deleteAccount_WhenAccountNotFound_ShouldThrow() {
        when(accountRepository.findById("non-existent")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> bankService.deleteAccount("non-existent"));
    }

    // Аналогично для Category
    @Test
    void updateCategory_WhenCategoryNotFound_ShouldThrow() {
        when(categoryRepository.findById("non-existent")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> bankService.updateCategory("non-existent", "new", "desc"));
    }

    @Test
    void deleteCategory_WhenMultipleOperationsOnlyOneWithCategory_ShouldThrowException() {
        // Подготавливаем данные
        Category cat = new Category("Категория", OperationType.EXPENSE, "");
        cat.setId("cat-1");
        BankAccount acc = new BankAccount("Счет", "RUB");
        acc.setId("acc-1");
        Operation op1 = new Operation(OperationType.EXPENSE, acc, new BigDecimal("100"), "тест1", cat);
        op1.setId("op-1");
        Operation op2 = new Operation(OperationType.EXPENSE, acc, new BigDecimal("200"), "тест2", null);
        op2.setId("op-2");

        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(cat));
        when(operationRepository.findByCategoryId("cat-1")).thenReturn(List.of(op1)); // одна операция с категорией

        assertThrows(IllegalStateException.class, () -> bankService.deleteCategory("cat-1"));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void recalculateAllBalances_WhenNoAccounts_ShouldDoNothing() {
        when(accountRepository.findAll()).thenReturn(List.of());
        bankService.recalculateAllBalances();
        verify(accountRepository, never()).save(any());
    }
}