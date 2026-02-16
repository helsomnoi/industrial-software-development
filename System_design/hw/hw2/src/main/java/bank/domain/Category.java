package bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String id;
    private String name;
    private OperationType type;
    private String description;
    private LocalDateTime createdAt;

    public Category(String name, OperationType type, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}