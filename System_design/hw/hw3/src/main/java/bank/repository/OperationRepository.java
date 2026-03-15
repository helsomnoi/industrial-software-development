package bank.repository;

import bank.domain.Operation;
import bank.domain.OperationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OperationRepository {
    Operation save(Operation operation);
    Optional<Operation> findById(String id);
    List<Operation> findByAccountId(String accountId);
    List<Operation> findByAccountIdAndDateBetween(String accountId, LocalDateTime from, LocalDateTime to);
    List<Operation> findAll();

    List<Operation> findByCategoryId(String categoryId);

    void deleteById(String id);

    BigDecimal sumByAccountAndTypeAndDateBetween(String accountId, OperationType type, LocalDateTime from, LocalDateTime to);
    List<Object[]> groupByCategory(String accountId, OperationType type, LocalDateTime from, LocalDateTime to);
}