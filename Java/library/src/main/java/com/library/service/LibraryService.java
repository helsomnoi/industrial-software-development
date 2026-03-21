package com.library.service;

import com.library.dao.*;
import com.library.dao.jdbc.*;
import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Reader;

import java.time.LocalDateTime;
import java.util.List;

// Основной сервис библиотеки, объединяющий DAO
public class LibraryService {
    private final BookDao bookDao = new BookDaoJdbc();
    private final ReaderDao readerDao = new ReaderDaoJdbc();
    private final LoanDao loanDao = new LoanDaoJdbc();

    public Book addBook(Book book) {
        return bookDao.save(book);
    }

    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    public List<Book> findBooksByTitle(String title) {
        return bookDao.findByTitle(title);
    }

    public Reader registerReader(Reader reader) {
        // Проверяем, не существует ли читатель с таким email
        if (readerDao.findByEmail(reader.getEmail()).isPresent()) {
            throw new LibraryException("Читатель с таким email уже зарегистрирован");
        }
        return readerDao.save(reader);
    }

    public List<Reader> getAllReaders() {
        return readerDao.findAll();
    }

    public void issueBook(int bookId, int readerId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new LibraryException("Книга с id " + bookId + " не найдена"));
        Reader reader = readerDao.findById(readerId)
                .orElseThrow(() -> new LibraryException("Читатель с id " + readerId + " не найден"));

        if (loanDao.isBookLoaned(bookId)) {
            throw new LibraryException("Книга \"" + book.getTitle() + "\" уже выдана другому читателю");
        }

        Loan loan = Loan.builder()
                .bookId(bookId)
                .readerId(readerId)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(30)) // на 30 дней
                .build();

        loanDao.save(loan);
        System.out.println("Книга \"" + book.getTitle() + "\" выдана читателю " + reader.getName());
    }

    public void returnBook(int loanId) {
        loanDao.returnLoan(loanId);
        System.out.println("Книга возвращена (ID выдачи: " + loanId + ")");
    }

    public List<Loan> getActiveLoansByReader(int readerId) {
        return loanDao.findActiveByReader(readerId);
    }

    public List<Book> getMostPopularBooks(int limit) {
        return loanDao.findMostPopularBooks(limit);
    }

    public List<Loan> getAllActiveLoans() {
        return loanDao.findAllActive();
    }
}