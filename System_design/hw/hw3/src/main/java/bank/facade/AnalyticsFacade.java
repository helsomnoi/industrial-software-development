package bank.facade;

import bank.domain.BankAccount;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.dto.AnalyticsReport;
import bank.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class AnalyticsFacade {
    private final OperationRepository operationRepository;
    private final AccountFacade accountFacade;
    private final CategoryFacade categoryFacade;

    public AnalyticsReport generateReport(String accountId,
                                          LocalDateTime from,
                                          LocalDateTime to){
        BankAccount account = accountFacade.getAccount(accountId);
        List<Operation> operations = operationRepository.findByAccountIdAndDateBetween(accountId, from, to);
        BigDecimal totalIncome = calculateTotal(operations, OperationType.INCOME);
        BigDecimal totalExpense = calculateTotal(operations, OperationType.EXPENSE);
        Map<String,BigDecimal> incomeByCategory = groupByCategory(operations, OperationType.INCOME);
        Map<String,BigDecimal> expenseByCategory = groupByCategory(operations, OperationType.EXPENSE);

        return new AnalyticsReport(account, from, to, totalIncome, totalExpense,
                incomeByCategory, expenseByCategory);
    }

    private Map<String, BigDecimal> groupByCategory(List<Operation> operations, OperationType type) {
        return operations.stream()
                .filter(op -> op.getType() == type && op.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        op -> categoryFacade.getCategory(op.getCategoryId()).getName(),
                        Collectors.mapping(Operation::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    private BigDecimal calculateTotal(List<Operation> operations, OperationType operationType){
        return operations.stream()
                .filter(op -> op.getType() == operationType)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
