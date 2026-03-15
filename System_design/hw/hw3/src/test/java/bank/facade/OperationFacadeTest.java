package bank.facade;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.factory.Impl.OperationFactory;
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
class OperationFacadeTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationFactory operationFactory;

    @Mock
    private AccountFacade accountFacade;

    @Mock
    private CategoryFacade categoryFacade;

    @InjectMocks
    private OperationFacade operationFacade;

    private BankAccount account;
    private Category category;
    private Operation operation;

    @BeforeEach
    void setUp() {
        account = new BankAccount("Счет", "RUB");
        account.setId("acc-1");
        category = new Category("Категория", OperationType.INCOME, "");
        category.setId("cat-1");
        operation = new Operation(OperationType.INCOME, account, new BigDecimal("1000"), "тест", category);
        operation.setId("op-1");
    }

    @Test
    void createOperation_Success() {
        BigDecimal amount = new BigDecimal("1000");
        when(accountFacade.getAccount("acc-1")).thenReturn(account);
        when(categoryFacade.getCategory("cat-1")).thenReturn(category);
        when(operationFactory.create(
                eq(OperationType.INCOME),
                eq(account),
                eq(amount),
                eq("тест"),
                eq(category)
        )).thenReturn(operation);
        doNothing().when(accountFacade).recalculateBalance("acc-1");
        when(operationRepository.save(any())).thenReturn(operation);

        Operation result = operationFacade.createOperation(OperationType.INCOME, "acc-1",
                amount, "тест", "cat-1");

        assertNotNull(result);
        verify(accountFacade).recalculateBalance("acc-1");
        verify(operationRepository).save(operation);
    }

    @Test
    void createOperation_WithoutCategory_Success() {
        BigDecimal amount = new BigDecimal("777");
        when(accountFacade.getAccount("acc-1")).thenReturn(account);
        when(operationFactory.create(
                eq(OperationType.INCOME),
                eq(account),
                eq(amount),
                eq("тест"),
                isNull()
        )).thenReturn(operation);
        doNothing().when(accountFacade).recalculateBalance("acc-1");
        when(operationRepository.save(any())).thenReturn(operation);

        Operation result = operationFacade.createOperation(OperationType.INCOME, "acc-1",
                amount, "тест", null);

        assertNotNull(result);
        verify(operationRepository).save(operation);
    }

    @Test
    void getAccountOperations_ShouldReturnList() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        when(operationRepository.findByAccountIdAndDateBetween("acc-1", from, to))
                .thenReturn(List.of(operation));

        List<Operation> ops = operationFacade.getAccountOperations("acc-1", from, to);
        assertEquals(1, ops.size());
        assertEquals(operation, ops.get(0));
    }

    @Test
    void getAllOperations_ShouldReturnList() {
        when(operationRepository.findAll()).thenReturn(List.of(operation));
        List<Operation> ops = operationFacade.getAllOperations();
        assertEquals(1, ops.size());
    }

    @Test
    void deleteOperation_ExistingOperation_ShouldDelete() {
        when(operationRepository.findById("op-1")).thenReturn(Optional.of(operation));
        doNothing().when(operationRepository).deleteById("op-1");

        operationFacade.deleteOperation("op-1");

        verify(operationRepository).deleteById("op-1");
    }

    @Test
    void deleteOperation_NonExisting_ShouldThrow() {
        when(operationRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> operationFacade.deleteOperation("non-existent"));
    }
}