package com.library.dao.jdbc;

import com.library.dao.BookDao;
import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDaoJdbc implements BookDao {

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, published_year) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());

            if (book.getPublishedYear() != null) {
                ps.setInt(4, book.getPublishedYear());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                book.setId(rs.getInt(1));
            }
            return book;

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при сохранении книги", e);
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapBook(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Ошибка при получении списка книг", e);
        }
        return books;
    }

    @Override
    public List<Book> findByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + title + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(mapBook(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Ошибка при поиске книг по названию", e);
        }
        return books;
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapBook(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при поиске книги по id", e);
        }
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return Book.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .isbn(rs.getString("isbn"))
                .publishedYear(rs.getInt("published_year"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}