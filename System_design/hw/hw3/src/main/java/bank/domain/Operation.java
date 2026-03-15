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
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    private String id;
    private OperationType type;
    private String bankAccountId;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
    private String categoryId;
    private LocalDateTime createdAt;

    @JsonIgnore
    private transient BankAccount bankAccount;
    @JsonIgnore
    private transient Category category;

    public Operation(OperationType type, BankAccount bankAccount, BigDecimal amount,
                     String description, Category category) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.bankAccountId = bankAccount.getId();
        this.bankAccount = bankAccount;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.date = LocalDateTime.now();
        this.description = description;
        this.categoryId = category != null ? category.getId() : null;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
        this.bankAccountId = bankAccount != null ? bankAccount.getId() : null;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public void applyToAccount() {
        if (bankAccount == null) {
            throw new IllegalStateException("Счет не установлен для операции");
        }
        if (type == OperationType.INCOME) {
            bankAccount.deposit(amount);
        } else {
            bankAccount.withdraw(amount);
        }
    }
}