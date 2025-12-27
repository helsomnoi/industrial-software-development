import java.time.LocalDateTime;

public class Transaction{
    public enum transactionType {
        DEPOSIT, WITHDRAW, TRANSFER
    };
    private transactionType type;
    private double amount;
    private Integer fromAccountNumber; // (может быть null)
    private Integer toAccountNumber; // (может быть null)
    private LocalDateTime timestamp; // (можно LocalDateTime)
    private boolean success;
    private String message;    // (строка с причиной ошибки или “OK”)

    public Transaction(transactionType type, double amount,
                       Integer fromAccountNumber, Integer toAccountNumber){
        this.type = type;
        this.amount = amount;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.timestamp = LocalDateTime.now(); // автоматически устанавливаем текущее время
        this.success = true; // по умолчанию успешно
        this.message = "OK"; // по умолчанию ок
    }

    public transactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public Integer getFromAccountNumber() {
        return fromAccountNumber;
    }

    public Integer getToAccountNumber() {
        return toAccountNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return timestamp + " | " + type + " | Сумма: " + amount +
                " | От: " + (fromAccountNumber != null ? fromAccountNumber : "-") +
                " | К: " + (toAccountNumber != null ? toAccountNumber : "-") +
                " | Статус: " + (success ? "Успешно" : "Ошибка") +
                " | Сообщение: " + message;
    }
}