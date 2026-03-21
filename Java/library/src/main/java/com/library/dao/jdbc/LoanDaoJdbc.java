package com.library.dao.jdbc;

import com.library.dao.LoanDao;
import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanDaoJdbc implements LoanDao {

    @Override
    public Loan save(Loan loan) {
        String sql = "INSERT INTO loans (book_id, reader_id, loan_date, due_date) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loan.getBookId());
            ps.setInt(2, loan.getReaderId());
            ps.setTimestamp(3, Timestamp.valueOf(loan.getLoanDate() != null ? loan.getLoanDate() : LocalDateTime.now()));

            if (loan.getDueDate() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(loan.getDueDate()));
            } else {
                // по умолчанию выдаём на 30 дней
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().plusDays(30)));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loan.setId(rs.getInt(1));
            }
            return loan;

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при выдаче книги", e);
        }
    }

    @Override
    public void returnLoan(int loanId) {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ? AND return_date IS NULL";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, loanId);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new LibraryException("Выдача с id " + loanId + " не найдена или уже возвращена");
            }

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при возврате книги", e);
        }
    }

    @Override
    public boolean isBookLoaned(int bookId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE book_id = ? AND return_date IS NULL";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при проверке выдачи книги", e);
        }
    }

    @Override
    public List<Loan> findActiveByReader(int readerId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE reader_id = ? AND return_date IS NULL ORDER BY loan_date";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, readerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                loans.add(mapLoan(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Ошибка при получении активных выдач читателя", e);
        }
        return loans;
    }

    @Override
    public List<Loan> findAllActive() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE return_date IS NULL ORDER BY loan_date";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(mapLoan(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Ошибка при получении списка выданных книг", e);
        }
        return loans;
    }

    @Override
    public List<Book> findMostPopularBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT b.*, COUNT(l.id) as loan_count
            FROM books b
            LEFT JOIN loans l ON b.id = l.book_id
            GROUP BY b.id
            ORDER BY loan_count DESC, b.title
            LIMIT ?
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(Book.builder()
                        .id(rs.getInt("id"))
                        .title(rs.getString("title"))
                        .author(rs.getString("author"))
                        .isbn(rs.getString("isbn"))
                        .publishedYear(rs.getInt("published_year"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build());
            }
        } catch (SQLException e) {
            throw new LibraryException("Ошибка при получении популярных книг", e);
        }
        return books;
    }

    private Loan mapLoan(ResultSet rs) throws SQLException {
        Timestamp returnDateTs = rs.getTimestamp("return_date");
        return Loan.builder()
                .id(rs.getInt("id"))
                .bookId(rs.getInt("book_id"))
                .readerId(rs.getInt("reader_id"))
                .loanDate(rs.getTimestamp("loan_date").toLocalDateTime())
                .returnDate(returnDateTs != null ? returnDateTs.toLocalDateTime() : null)
                .dueDate(rs.getTimestamp("due_date") != null ? rs.getTimestamp("due_date").toLocalDateTime() : null)
                .build();
    }
}