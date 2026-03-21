package com.library.dao;

import com.library.model.Book;
import com.library.model.Loan;

import java.util.List;

public interface LoanDao {
    Loan save(Loan loan);
    void returnLoan(int loanId);
    boolean isBookLoaned(int bookId);
    List<Loan> findActiveByReader(int readerId);
    List<Loan> findAllActive();
    List<Book> findMostPopularBooks(int limit);
}