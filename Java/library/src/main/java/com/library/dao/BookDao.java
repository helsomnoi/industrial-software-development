package com.library.dao;

import com.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    Book save(Book book);
    List<Book> findAll();
    List<Book> findByTitle(String title);
    Optional<Book> findById(int id);
}