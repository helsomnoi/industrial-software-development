package com.library.dao;

import com.library.model.Reader;

import java.util.List;
import java.util.Optional;

public interface ReaderDao {
    Reader save(Reader reader);
    List<Reader> findAll();
    Optional<Reader> findById(int id);
    Optional<Reader> findByEmail(String email);
}