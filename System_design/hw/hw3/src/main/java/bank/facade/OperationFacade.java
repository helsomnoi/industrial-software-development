package bank.facade;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.factory.Impl.OperationFactory;
import bank.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OperationFacade {
    private final OperationRepository operationRepository;
    private final OperationFactory operationFactory;
    private final AccountFacade accountFacade;
    private final CategoryFacade categoryFacade;

    public Operation createOperation(OperationType type, String accountId,
                                     BigDecimal amount, String description,
                                     String categoryId) {
        BankAccount account = accountFacade.getAccount(accountId);
        Category category = (categoryId != null) ? categoryFacade.getCategory(categoryId) : null;
        Operation operation = operationFactory.create(type, account, amount,
                description, category);
        account.addOperation(operation);
        accountFacade.recalculateBalance(accountId);
        return operationRepository.save(operation);
    }

    public List<Operation> getAccountOperations(String accountId, LocalDateTime from,
                                                LocalDateTime to) {
        return operationRepository.findByAccountIdAndDateBetween(accountId, from, to);
    }

    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    public void deleteOperation(String operationId) {
        operationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Операция не найдена: " + operationId));
        operationRepository.deleteById(operationId);
    }
}
