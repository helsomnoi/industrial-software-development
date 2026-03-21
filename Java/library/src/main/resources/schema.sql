-- Таблица книг
CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    isbn VARCHAR(20) UNIQUE,
    published_year INTEGER,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Таблица читателей
CREATE TABLE IF NOT EXISTS readers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    registered_at TIMESTAMP DEFAULT NOW()
);

-- Таблица выдач
CREATE TABLE IF NOT EXISTS loans (
    id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL REFERENCES books(id) ON DELETE RESTRICT,
    reader_id INTEGER NOT NULL REFERENCES readers(id) ON DELETE RESTRICT,
    loan_date TIMESTAMP DEFAULT NOW(),
    return_date TIMESTAMP,
    due_date TIMESTAMP
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_title_lower ON books(LOWER(title));
CREATE INDEX IF NOT EXISTS idx_loans_reader_id ON loans(reader_id);
CREATE INDEX IF NOT EXISTS idx_loans_book_id ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_return_date ON loans(return_date);