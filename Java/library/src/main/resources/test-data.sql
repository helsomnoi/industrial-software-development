-- Тестовые данные для библиотеки

-- Добавление книг
INSERT INTO books (title, author, isbn, published_year, created_at) VALUES
('Война и мир', 'Лев Толстой', '978-5-17-118914-9', 1869, NOW()),
('Преступление и наказание', 'Фёдор Достоевский', '978-5-17-119313-9', 1866, NOW()),
('Мастер и Маргарита', 'Михаил Булгаков', '978-5-17-119342-9', 1967, NOW()),
('1984', 'Джордж Оруэлл', '978-5-17-118915-6', 1949, NOW()),
('Маленький принц', 'Антуан де Сент-Экзюпери', '978-5-17-118916-3', 1943, NOW()),
('Гарри Поттер и философский камень', 'Дж.К. Роулинг', '978-5-389-14128-7', 1997, NOW()),
('Убить пересмешника', 'Харпер Ли', '978-5-17-118917-0', 1960, NOW()),
('Думай медленно... решай быстро', 'Даниэль Канеман', '978-5-17-118918-7', 2011, NOW()),
('Атлант расправил плечи', 'Айн Рэнд', '978-5-17-118919-4', 1957, NOW()),
('Стив Джобс', 'Уолтер Айзексон', '978-5-17-118920-0', 2011, NOW()),
('Гордость и предубеждение', 'Джейн Остин', '978-5-17-118921-7', 1813, NOW()),
('Три товарища', 'Эрих Мария Ремарк', '978-5-17-118922-4', 1936, NOW()),
('Над пропастью во ржи', 'Джером Д. Сэлинджер', '978-5-17-118923-1', 1951, NOW()),
('Марсианин', 'Энди Вейер', '978-5-17-118924-8', 2011, NOW()),
('Дюна', 'Фрэнк Герберт', '978-5-17-118925-5', 1965, NOW());

-- Читатели
INSERT INTO readers (name, email, registered_at) VALUES
('Иван Петров', 'ivan.petrov@example.com', NOW()),
('Мария Сидорова', 'maria.sidorova@example.com', NOW()),
('Алексей Смирнов', 'alexey.smirnov@example.com', NOW()),
('Елена Козлова', 'elena.kozlova@example.com', NOW()),
('Дмитрий Иванов', 'dmitry.ivanov@example.com', NOW()),
('Ольга Новикова', 'olga.novikova@example.com', NOW()),
('Сергей Морозов', 'sergey.morozov@example.com', NOW()),
('Татьяна Волкова', 'tatiana.volkova@example.com', NOW()),
('Андрей Соколов', 'andrey.sokolov@example.com', NOW()),
('Наталья Лебедева', 'natalia.lebedeva@example.com', NOW());


-- Выдачи
INSERT INTO loans (book_id, reader_id, loan_date, due_date, return_date) VALUES
(1, 1, NOW() - INTERVAL '10 days', NOW() + INTERVAL '20 days', NULL),
(2, 2, NOW() - INTERVAL '5 days', NOW() + INTERVAL '25 days', NULL),
(3, 3, NOW() - INTERVAL '1 day', NOW() + INTERVAL '29 days', NULL),
(4, 4, NOW() - INTERVAL '15 days', NOW() + INTERVAL '15 days', NULL),
(5, 5, NOW() - INTERVAL '2 days', NOW() + INTERVAL '28 days', NULL);

-- история
INSERT INTO loans (book_id, reader_id, loan_date, due_date, return_date) VALUES
(6, 6, NOW() - INTERVAL '40 days', NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days'),
(7, 7, NOW() - INTERVAL '50 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '15 days'),
(8, 8, NOW() - INTERVAL '60 days', NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days'),
(9, 9, NOW() - INTERVAL '70 days', NOW() - INTERVAL '40 days', NOW() - INTERVAL '35 days'),
(10, 10, NOW() - INTERVAL '80 days', NOW() - INTERVAL '50 days', NOW() - INTERVAL '45 days');


INSERT INTO loans (book_id, reader_id, loan_date, due_date, return_date) VALUES
(11, 1, NOW() - INTERVAL '3 days', NOW() + INTERVAL '27 days', NULL),
(12, 2, NOW() - INTERVAL '7 days', NOW() + INTERVAL '23 days', NULL),
(13, 3, NOW() - INTERVAL '12 days', NOW() + INTERVAL '18 days', NULL),
(14, 4, NOW() - INTERVAL '20 days', NOW() + INTERVAL '10 days', NULL),
(15, 5, NOW() - INTERVAL '25 days', NOW() + INTERVAL '5 days', NULL);