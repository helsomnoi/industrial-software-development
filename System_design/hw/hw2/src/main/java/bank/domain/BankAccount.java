package bank.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    private String id;
    private String name;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    @JsonIgnore
    private List<Operation> operations = new ArrayList<>();

    public BankAccount(String name, String currency) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.balance = BigDecimal.ZERO;
        this.currency = currency;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.operations = new ArrayList<>();
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        this.balance = this.balance.add(amount).setScale(2, RoundingMode.HALF_EVEN);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств на счете. Доступно: " + this.balance);
        }
        this.balance = this.balance.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);
        this.updatedAt = LocalDateTime.now();
    }

    public void recalculateBalance() {
        BigDecimal calculatedBalance = operations.stream()
                .map(op -> {
                    if (op.getType() == OperationType.INCOME) {
                        return op.getAmount();
                    } else {
                        return op.getAmount().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);

        if (this.balance.compareTo(calculatedBalance) != 0) {
            System.out.println("ВНИМАНИЕ: Обнаружено несоответствие баланса!");
            System.out.println("Текущий баланс: " + this.balance);
            System.out.println("Расчетный баланс: " + calculatedBalance);
            System.out.println("Разница: " + this.balance.subtract(calculatedBalance));

            this.balance = calculatedBalance;
            this.updatedAt = LocalDateTime.now();

            System.out.println("Баланс скорректирован до: " + this.balance);
        } else {
            System.out.println("Баланс корректен: " + this.balance);
        }
    }

    public boolean autoRecalculateIfNeeded() {
        BigDecimal calculatedBalance = operations.stream()
                .map(op -> {
                    if (op.getType() == OperationType.INCOME) {
                        return op.getAmount();
                    } else {
                        return op.getAmount().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);

        if (this.balance.subtract(calculatedBalance).abs().compareTo(new BigDecimal("0.01")) > 0) {
            System.out.println("Автоматический пересчет баланса для счета " + name);
            System.out.println("Было: " + this.balance + ", стало: " + calculatedBalance);
            this.balance = calculatedBalance;
            this.updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
        operation.applyToAccount();
        this.updatedAt = LocalDateTime.now();
    }
}