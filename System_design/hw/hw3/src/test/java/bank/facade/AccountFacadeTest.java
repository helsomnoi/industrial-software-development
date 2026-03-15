package bank.facade;

import bank.domain.BankAccount;
import bank.repository.AccountRepository;
import bank.factory.Impl.BankAccountFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountFacadeTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BankAccountFactory accountFactory;

    @InjectMocks
    private AccountFacade accountFacade;

    private BankAccount testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new BankAccount("Тест", "RUB");
        testAccount.setId("acc-1");
    }

    @Test
    void createAccount_Success() {
        when(accountRepository.existsByName("Основной")).thenReturn(false);
        when(accountFactory.create("Основной", "RUB")).thenReturn(testAccount);
        when(accountRepository.save(any())).thenReturn(testAccount);

        BankAccount result = accountFacade.createAccount("Основной", "RUB");

        assertNotNull(result);
        assertEquals(testAccount, result);
        verify(accountRepository).save(testAccount);
    }

    @Test
    void createAccount_DuplicateName_ThrowsException() {
        when(accountRepository.existsByName("Основной")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> accountFacade.createAccount("Основной", "RUB"));
        assertEquals("Счет с именем 'Основной' уже существует", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_Success() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        BankAccount updated = accountFacade.updateAccount("acc-1", "Новое имя");

        assertEquals("Новое имя", updated.getName());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void getAccount_NotFound_ThrowsException() {
        when(accountRepository.findById("non-existent")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> accountFacade.getAccount("non-existent"));
    }

    @Test
    void getAllAccounts_ShouldReturnList() {
        when(accountRepository.findAll()).thenReturn(List.of(testAccount));
        List<BankAccount> accounts = accountFacade.getAllAccounts();
        assertEquals(1, accounts.size());
        assertEquals(testAccount, accounts.get(0));
    }

    @Test
    void deleteAccount_Success() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        doNothing().when(accountRepository).deleteById("acc-1");

        accountFacade.deleteAccount("acc-1");
        verify(accountRepository).deleteById("acc-1");
    }

    @Test
    void recalculateBalance_ShouldCallRecalculateAndSave() {
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        accountFacade.recalculateBalance("acc-1");
        verify(accountRepository).save(testAccount);
    }
}