package bank.repository.Impl;

import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.OperationRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OperationRepositoryImpl implements OperationRepository {
    private final Map<String, Operation> storage = new ConcurrentHashMap<>();

    @Override
    public Operation save(Operation operation) {
        storage.put(operation.getId(), operation);
        return operation;
    }

    @Override
    public Optional<Operation> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Operation> findByAccountId(String accountId) {
        return storage.values().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Operation> findByAccountIdAndDateBetween(String accountId, LocalDateTime from, LocalDateTime to) {
        return storage.values().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .filter(op -> op.getDate().isAfter(from) && op.getDate().isBefore(to))
                .sorted(Comparator.comparing(Operation::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Operation> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Operation> findByCategoryId(String categoryId) {
        return storage.values().stream()
                .filter(op -> categoryId.equals(op.getCategoryId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }

    @Override
    public BigDecimal sumByAccountAndTypeAndDateBetween(String accountId, OperationType type,
                                                        LocalDateTime from, LocalDateTime to) {
        return storage.values().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .filter(op -> op.getType() == type)
                .filter(op -> op.getDate().isAfter(from) && op.getDate().isBefore(to))
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Object[]> groupByCategory(String accountId, OperationType type,
                                          LocalDateTime from, LocalDateTime to) {
        return storage.values().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .filter(op -> op.getType() == type)
                .filter(op -> op.getDate().isAfter(from) && op.getDate().isBefore(to))
                .filter(op -> op.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        Operation::getCategoryId,
                        Collectors.reducing(BigDecimal.ZERO, Operation::getAmount, BigDecimal::add)
                ))
                .entrySet().stream()
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .collect(Collectors.toList());
    }


}