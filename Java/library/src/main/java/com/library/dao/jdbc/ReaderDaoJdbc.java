package com.library.dao.jdbc;

import com.library.dao.ReaderDao;
import com.library.exception.LibraryException;
import com.library.model.Reader;
import com.library.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReaderDaoJdbc implements ReaderDao {

    @Override
    public Reader save(Reader reader) {
        String sql = "INSERT INTO readers (name, email) VALUES (?, ?) RETURNING id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reader.getName());
            ps.setString(2, reader.getEmail());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                reader.setId(rs.getInt(1));
            }
            return reader;

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при сохранении читателя", e);
        }
    }

    @Override
    public List<Reader> findAll() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT * FROM readers ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                readers.add(mapReader(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Ошибка при получении списка читателей", e);
        }
        return readers;
    }

    @Override
    public Optional<Reader> findById(int id) {
        String sql = "SELECT * FROM readers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapReader(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при поиске читателя по id", e);
        }
    }

    @Override
    public Optional<Reader> findByEmail(String email) {
        String sql = "SELECT * FROM readers WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapReader(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new LibraryException("Ошибка при поиске читателя по email", e);
        }
    }

    private Reader mapReader(ResultSet rs) throws SQLException {
        return Reader.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .registeredAt(rs.getTimestamp("registered_at").toLocalDateTime())
                .build();
    }
}