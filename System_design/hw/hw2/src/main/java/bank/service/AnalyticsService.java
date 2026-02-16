package bank.service;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.repository.AccountRepository;
import bank.repository.CategoryRepository;
import bank.repository.OperationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    public AnalyticsService(AccountRepository accountRepository,
                            CategoryRepository categoryRepository,
                            OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    public void printAnalytics(String accountId, LocalDateTime from, LocalDateTime to) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет не найден"));

        List<Operation> allOps = operationRepository.findByAccountId(accountId);
        System.out.println("Всего операций по счету: " + allOps.size());

        List<Operation> operations = operationRepository.findByAccountIdAndDateBetween(accountId, from, to);
        System.out.println("Операций за период: " + operations.size());

        System.out.println("\nАНАЛИТИКА ПО СЧЕТУ: " + account.getName());
        System.out.println("Период: " + from.toLocalDate() + " - " + to.toLocalDate());
        System.out.println("===========================================");

        BigDecimal totalIncome = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("Доходы: " + totalIncome + " " + account.getCurrency());
        System.out.println("Расходы: " + totalExpense + " " + account.getCurrency());
        System.out.println("Итого: " + totalIncome.subtract(totalExpense) + " " + account.getCurrency());

        // Группировка по категориям
        Map<String, BigDecimal> incomeByCategory = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME && op.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        op -> categoryRepository.findById(op.getCategoryId())
                                .map(Category::getName).orElse("Без категории"),
                        Collectors.mapping(Operation::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        Map<String, BigDecimal> expenseByCategory = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE && op.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        op -> categoryRepository.findById(op.getCategoryId())
                                .map(Category::getName).orElse("Без категории"),
                        Collectors.mapping(Operation::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        System.out.println("\nДоходы по категориям:");
        incomeByCategory.forEach((cat, sum) -> System.out.println("  " + cat + ": " + sum));

        System.out.println("Расходы по категориям:");
        expenseByCategory.forEach((cat, sum) -> System.out.println("  " + cat + ": " + sum));

        System.out.println("\nТОП-5 КРУПНЫХ ОПЕРАЦИЙ:");
        operations.stream()
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .limit(5)
                .forEach(op -> {
                    String catName = op.getCategoryId() != null
                            ? categoryRepository.findById(op.getCategoryId())
                            .map(Category::getName).orElse("Без категории")
                            : "Без категории";
                    System.out.printf("  %s | %s | %s %s | %s%n",
                            op.getDate().toLocalDate(),
                            op.getType(),
                            op.getAmount(),
                            account.getCurrency(),
                            catName);
                });
        System.out.println("===========================================\n");
    }
}