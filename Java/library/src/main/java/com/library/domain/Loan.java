package com.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Лог выдачи/возврата
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    private int id;
    private int bookId;
    private int readerId;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private LocalDateTime dueDate;
}