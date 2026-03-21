package com.library.console;

import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Reader;
import com.library.service.LibraryService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsoleRun {
    private final LibraryService service = new LibraryService();
    private final Scanner scanner;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public ConsoleRun(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("=== Библиотечная система ===");
        System.out.println("Проверка подключения к базе данных...");
        try {
            service.getAllBooks();
            System.out.println("Подключение установлено.\n");
        } catch (Exception e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            System.err.println("Проверьте параметры в application.properties и запуск PostgreSQL.");
            return;
        }

        while (true) {
            printMainMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> bookMenu();
                case 2 -> readerMenu();
                case 3 -> loanMenu();
                case 4 -> statisticsMenu();
                case 0 -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Неверный выбор. Пожалуйста, выберите пункт от 0 до 4.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Работа с книгами");
        System.out.println("2. Работа с читателями");
        System.out.println("3. Операции выдачи");
        System.out.println("4. Статистика");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void bookMenu() {
        while (true) {
            System.out.println("\n--- Книги ---");
            System.out.println("1. Добавить книгу");
            System.out.println("2. Список всех книг");
            System.out.println("3. Найти книгу по названию");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");
            int choice = readInt();
            switch (choice) {
                case 1 -> addBook();
                case 2 -> listBooks(service.getAllBooks());
                case 3 -> findBookByTitle();
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void addBook() {
        System.out.println("\n--- Добавление книги ---");
        System.out.print("Название: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Название не может быть пустым.");
            return;
        }
        System.out.print("Автор: ");
        String author = scanner.nextLine().trim();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Год издания (целое число, можно оставить пустым): ");
        String yearStr = scanner.nextLine().trim();
        Integer year = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);

        Book book = Book.builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .publishedYear(year)
                .build();

        try {
            service.addBook(book);
            System.out.println("Книга успешно добавлена (ID: " + book.getId() + ")");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении книги: " + e.getMessage());
        }
    }

    private void listBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Список книг пуст.");
            return;
        }
        System.out.println("\n=== Список книг ===");
        for (Book b : books) {
            System.out.printf("ID: %d | %s | %s | ISBN: %s | %d%n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getPublishedYear());
        }
    }

    private void findBookByTitle() {
        System.out.print("\nВведите название (или часть): ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Название не введено.");
            return;
        }
        List<Book> books = service.findBooksByTitle(title);
        if (books.isEmpty()) {
            System.out.println("Книги с таким названием не найдены.");
        } else {
            listBooks(books);
        }
    }

    private void readerMenu() {
        while (true) {
            System.out.println("\n--- Читатели ---");
            System.out.println("1. Зарегистрировать читателя");
            System.out.println("2. Список всех читателей");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");
            int choice = readInt();
            switch (choice) {
                case 1 -> registerReader();
                case 2 -> listReaders(service.getAllReaders());
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void registerReader() {
        System.out.println("\n--- Регистрация читателя ---");
        System.out.print("ФИО: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("ФИО не может быть пустым.");
            return;
        }
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Email не может быть пустым.");
            return;
        }

        Reader reader = Reader.builder()
                .name(name)
                .email(email)
                .build();

        try {
            service.registerReader(reader);
            System.out.println("Читатель успешно зарегистрирован (ID: " + reader.getId() + ")");
        } catch (Exception e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
        }
    }

    private void listReaders(List<Reader> readers) {
        if (readers.isEmpty()) {
            System.out.println("Список читателей пуст.");
            return;
        }
        System.out.println("\n=== Список читателей ===");
        for (Reader r : readers) {
            System.out.printf("ID: %d | %s | %s%n", r.getId(), r.getName(), r.getEmail());
        }
    }

    private void loanMenu() {
        while (true) {
            System.out.println("\n--- Операции выдачи ---");
            System.out.println("1. Выдать книгу читателю");
            System.out.println("2. Вернуть книгу");
            System.out.println("3. Список книг, выданных читателю");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");
            int choice = readInt();
            switch (choice) {
                case 1 -> issueBook();
                case 2 -> returnBook();
                case 3 -> showLoansByReader();
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void issueBook() {
        System.out.println("\n--- Выдача книги ---");

        List<Book> allBooks = service.getAllBooks();
        List<Loan> activeLoans = service.getAllActiveLoans();
        List<Book> availableBooks = allBooks.stream()
                .filter(book -> activeLoans.stream()
                        .noneMatch(loan -> loan.getBookId() == book.getId()))
                .toList();

        if (availableBooks.isEmpty()) {
            System.out.println("Нет доступных книг для выдачи.");
            return;
        }

        System.out.println("\n=== Доступные книги ===");
        for (Book book : availableBooks) {
            System.out.printf("ID: %d | %s | %s%n", book.getId(), book.getTitle(), book.getAuthor());
        }
        System.out.print("\nВыберите ID книги: ");
        int bookId = readInt();

        List<Reader> readers = service.getAllReaders();
        if (readers.isEmpty()) {
            System.out.println("Нет зарегистрированных читателей.");
            return;
        }

        System.out.println("\n=== Список читателей ===");
        for (Reader reader : readers) {
            System.out.printf("ID: %d | %s | %s%n", reader.getId(), reader.getName(), reader.getEmail());
        }
        System.out.print("\nВыберите ID читателя: ");
        int readerId = readInt();

        try {
            service.issueBook(bookId, readerId);
        } catch (Exception e) {
            System.err.println("Ошибка при выдаче: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.println("\n--- Возврат книги ---");
        List<Loan> activeLoans = service.getAllActiveLoans();
        if (activeLoans.isEmpty()) {
            System.out.println("Нет активных выдач.");
            return;
        }

        System.out.println("\n=== Активные выдачи ===");
        for (Loan loan : activeLoans) {
            Book book = service.getAllBooks().stream()
                    .filter(b -> b.getId() == loan.getBookId())
                    .findFirst().orElse(null);
            Reader reader = service.getAllReaders().stream()
                    .filter(r -> r.getId() == loan.getReaderId())
                    .findFirst().orElse(null);
            System.out.printf("ID выдачи: %d | Книга: %s | Читатель: %s | Дата: %s%n",
                    loan.getId(),
                    book != null ? book.getTitle() : "Не найдено",
                    reader != null ? reader.getName() : "Не найден",
                    loan.getLoanDate().format(dateFormat));
        }
        System.out.print("\nВведите ID выдачи для возврата: ");
        int loanId = readInt();

        try {
            service.returnBook(loanId);
        } catch (Exception e) {
            System.err.println("Ошибка при возврате: " + e.getMessage());
        }
    }

    private void showLoansByReader() {
        List<Reader> readers = service.getAllReaders();
        if (readers.isEmpty()) {
            System.out.println("Нет зарегистрированных читателей.");
            return;
        }

        System.out.println("\n=== Список читателей ===");
        for (Reader reader : readers) {
            System.out.printf("ID: %d | %s | %s%n", reader.getId(), reader.getName(), reader.getEmail());
        }
        System.out.print("\nВведите ID читателя: ");
        int readerId = readInt();

        List<Loan> loans = service.getActiveLoansByReader(readerId);
        if (loans.isEmpty()) {
            System.out.println("У данного читателя нет активных выдач.");
            return;
        }

        System.out.println("\n=== Активные выдачи читателя ===");
        for (Loan l : loans) {
            Book book = service.getAllBooks().stream()
                    .filter(b -> b.getId() == l.getBookId())
                    .findFirst().orElse(null);
            System.out.printf("ID выдачи: %d | Книга: %s | Дата выдачи: %s | Срок: %s%n",
                    l.getId(),
                    book != null ? book.getTitle() : "Не найдено",
                    l.getLoanDate().format(dateFormat),
                    l.getDueDate() != null ? l.getDueDate().format(dateFormat) : "не указан");
        }
    }

    private void statisticsMenu() {
        while (true) {
            System.out.println("\n--- Статистика ---");
            System.out.println("1. Популярные книги");
            System.out.println("2. Список выданных книг (активные выдачи)");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");
            int choice = readInt();
            switch (choice) {
                case 1 -> showPopularBooks();
                case 2 -> showAllActiveLoans();
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void showPopularBooks() {
        System.out.print("\nСколько книг показать? (например, 5): ");
        int limit = readInt();
        if (limit <= 0) limit = 10;
        List<Book> popular = service.getMostPopularBooks(limit);
        if (popular.isEmpty()) {
            System.out.println("Нет данных о выдаче книг.");
            return;
        }
        System.out.println("\n=== Популярные книги (по количеству выдач) ===");
        for (Book b : popular) {
            System.out.printf("ID: %d | %s | %s | ISBN: %s%n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn());
        }
    }

    private void showAllActiveLoans() {
        List<Loan> loans = service.getAllActiveLoans();
        if (loans.isEmpty()) {
            System.out.println("Нет выданных книг (все книги в библиотеке).");
            return;
        }
        System.out.println("\n=== Все выданные книги (активные выдачи) ===");
        for (Loan l : loans) {
            Book book = service.getAllBooks().stream()
                    .filter(b -> b.getId() == l.getBookId())
                    .findFirst().orElse(null);
            Reader reader = service.getAllReaders().stream()
                    .filter(r -> r.getId() == l.getReaderId())
                    .findFirst().orElse(null);
            System.out.printf("ID выдачи: %d | Книга: %s | Читатель: %s | Дата выдачи: %s | Срок: %s%n",
                    l.getId(),
                    book != null ? book.getTitle() : "Не найдено",
                    reader != null ? reader.getName() : "Не найден",
                    l.getLoanDate().format(dateFormat),
                    l.getDueDate() != null ? l.getDueDate().format(dateFormat) : "не указан");
        }
    }

    private int readInt() {
        while (true) {
            if (!scanner.hasNextLine()) {
                throw new LibraryException("Нет ввода от пользователя");
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.print("Введите целое число: ");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число: ");
            }
        }
    }
}