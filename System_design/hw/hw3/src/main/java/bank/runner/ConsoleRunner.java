package bank.runner;

import bank.domain.OperationType;
import bank.dto.AnalyticsReport;
import bank.facade.AccountFacade;
import bank.facade.AnalyticsFacade;
import bank.facade.CategoryFacade;
import bank.facade.ExportFacade;
import bank.facade.OperationFacade;
import bank.command.Command;
import bank.command.CommandFactory;
import bank.data_operations.importer.CsvDataImporter;
import bank.data_operations.importer.JsonDataImporter;
import bank.data_operations.importer.YamlDataImporter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Set;

@Component
@Profile("!test")
public class ConsoleRunner implements CommandLineRunner {

    private final AccountFacade accountFacade;
    private final CategoryFacade categoryFacade;
    private final OperationFacade operationFacade;
    private final AnalyticsFacade analyticsFacade;
    private final ExportFacade exportFacade;
    private final JsonDataImporter jsonDataImporter;
    private final YamlDataImporter yamlDataImporter;
    private final CsvDataImporter csvDataImporter;
    private final CommandFactory commandFactory;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleRunner(AccountFacade accountFacade,
                         CategoryFacade categoryFacade,
                         OperationFacade operationFacade,
                         AnalyticsFacade analyticsFacade,
                         ExportFacade exportFacade,
                         JsonDataImporter jsonDataImporter,
                         YamlDataImporter yamlDataImporter,
                         CsvDataImporter csvDataImporter,
                         CommandFactory commandFactory) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
        this.analyticsFacade = analyticsFacade;
        this.exportFacade = exportFacade;
        this.jsonDataImporter = jsonDataImporter;
        this.yamlDataImporter = yamlDataImporter;
        this.csvDataImporter = csvDataImporter;
        this.commandFactory = commandFactory;
    }

    @Override
    public void run(String... args) {
        System.out.println("\nБАНКОВСКАЯ СИСТЕМА УПРАВЛЕНИЯ СЧЕТАМИ");
        System.out.println("===========================================");

        createTestData();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> manageAccounts();
                case "2" -> manageCategories();
                case "3" -> manageOperations();
                case "4" -> showAnalytics();
                case "5" -> importExportMenu();
                case "6" -> checkAndRecalculateBalances();
                case "0" -> {
                    running = false;
                    System.out.println("До свидания!");
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createTestData() {
        System.out.println("\nСоздание тестовых данных...");

        // Создание категорий через фасад
        categoryFacade.create("Зарплата", OperationType.INCOME, "Основной доход");
        categoryFacade.create("Кэшбэк", OperationType.INCOME, "Бонусы от банка");
        categoryFacade.create("Продукты", OperationType.EXPENSE, "Еда и напитки");
        categoryFacade.create("Транспорт", OperationType.EXPENSE, "Такси и общественный транспорт");
        categoryFacade.create("Рестораны", OperationType.EXPENSE, "Кафе и рестораны");

        // Создание счетов через фасад
        var mainAccount = accountFacade.createAccount("Основной счет", "RUB");
        var savingsAccount = accountFacade.createAccount("Накопительный", "RUB");

        // Создание операций через фасад
        var incomeCategories = categoryFacade.getCategoriesByType(OperationType.INCOME);
        var expenseCategories = categoryFacade.getCategoriesByType(OperationType.EXPENSE);

        operationFacade.createOperation(
                OperationType.INCOME,
                mainAccount.getId(),
                new BigDecimal("100000"),
                "Зарплата за январь",
                incomeCategories.get(0).getId()
        );

        operationFacade.createOperation(
                OperationType.EXPENSE,
                mainAccount.getId(),
                new BigDecimal("15000"),
                "Продукты на месяц",
                expenseCategories.get(0).getId()
        );

        operationFacade.createOperation(
                OperationType.EXPENSE,
                mainAccount.getId(),
                new BigDecimal("5000"),
                "Такси",
                expenseCategories.get(1).getId()
        );

        operationFacade.createOperation(
                OperationType.INCOME,
                savingsAccount.getId(),
                new BigDecimal("5000"),
                "Перевод с основного",
                null
        );

        // Вывод созданных данных
        printAllAccounts();
        printAllCategories();
        printAllOperations();

        System.out.println("Тестовые данные созданы\n");
    }

    private void printMainMenu() {
        System.out.println("\nГЛАВНОЕ МЕНЮ");
        System.out.println("1. Управление счетами");
        System.out.println("2. Управление категориями");
        System.out.println("3. Управление операциями");
        System.out.println("4. Аналитика");
        System.out.println("5. Импорт/Экспорт");
        System.out.println("6. Проверка и пересчет балансов");
        System.out.println("0. Выход");
        System.out.print("Выберите пункт: ");
    }

    private void manageAccounts() {
        boolean back = false;
        while (!back) {
            System.out.println("\nУПРАВЛЕНИЕ СЧЕТАМИ");
            printAllAccounts();
            System.out.println("1. Создать счет");
            System.out.println("2. Редактировать счет");
            System.out.println("3. Удалить счет");
            System.out.println("4. Пересчитать баланс счета");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    System.out.print("Название счета: ");
                    String name = scanner.nextLine();
                    String currency = readCurrency();

                    // Используем команду через фабрику
                    Command command = commandFactory.createCreateAccountCommand(name, currency);
                    command.execute();
                }
                case "2" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String id = scanner.nextLine();
                    System.out.print("Новое название: ");
                    String newName = scanner.nextLine();
                    try {
                        accountFacade.updateAccount(id, newName);
                        System.out.println("Счет обновлен");
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "3" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String id = scanner.nextLine();
                    try {
                        accountFacade.deleteAccount(id);
                        System.out.println("Счет удален");
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "4" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String id = scanner.nextLine();

                    // Используем команду для пересчета
                    Command command = commandFactory.createRecalculateBalanceCommand(id);
                    command.execute();
                }
                case "0" -> back = true;
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void manageCategories() {
        boolean back = false;
        while (!back) {
            System.out.println("\nУПРАВЛЕНИЕ КАТЕГОРИЯМИ");
            printAllCategories();
            System.out.println("1. Создать категорию");
            System.out.println("2. Редактировать категорию");
            System.out.println("3. Удалить категорию");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    System.out.print("Название категории: ");
                    String name = scanner.nextLine();
                    System.out.print("Тип (INCOME/EXPENSE): ");
                    OperationType type = readOperationType();
                    System.out.print("Описание: ");
                    String desc = scanner.nextLine();

                    // Используем команду через фабрику
                    Command command = commandFactory.createCreateCategoryCommand(name, type, desc);
                    command.execute();
                }
                case "2" -> {
                    printAllCategories();
                    System.out.print("ID категории: ");
                    String id = scanner.nextLine();
                    System.out.print("Новое название: ");
                    String newName = scanner.nextLine();
                    System.out.print("Новое описание: ");
                    String newDesc = scanner.nextLine();
                    try {
                        categoryFacade.updateCategory(id, newName, newDesc);
                        System.out.println("Категория обновлена");
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "3" -> {
                    printAllCategories();
                    System.out.print("ID категории: ");
                    String id = scanner.nextLine();
                    try {
                        categoryFacade.deleteCategory(id);
                        System.out.println("Категория удалена");
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "0" -> back = true;
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void manageOperations() {
        boolean back = false;
        while (!back) {
            System.out.println("\nУПРАВЛЕНИЕ ОПЕРАЦИЯМИ");
            System.out.println("1. Создать операцию");
            System.out.println("2. Просмотреть операции счета");
            System.out.println("3. Просмотреть все операции");
            System.out.println("4. Удалить операцию");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String accountId = scanner.nextLine();

                    OperationType type = readOperationType();

                    System.out.print("Сумма: ");
                    BigDecimal amount = new BigDecimal(scanner.nextLine());

                    System.out.print("Описание: ");
                    String desc = scanner.nextLine();

                    printAllCategories();
                    System.out.print("ID категории (Enter для пропуска): ");
                    String catId = scanner.nextLine();
                    catId = catId.isEmpty() ? null : catId;

                    // Используем команду через фабрику
                    Command command = commandFactory.createCreateOperationCommand(
                            type, accountId, amount, desc, catId
                    );
                    command.execute();
                }
                case "2" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String accountId = scanner.nextLine();

                    LocalDateTime from = readDate("Начало периода (ГГГГ-ММ-ДД): ");
                    LocalDateTime to = readDate("Конец периода (ГГГГ-ММ-ДД): ").plusDays(1).minusNanos(1);

                    var operations = operationFacade.getAccountOperations(accountId, from, to);
                    System.out.println("\nОПЕРАЦИИ ЗА ПЕРИОД:");
                    if (operations.isEmpty()) {
                        System.out.println("Нет операций за выбранный период");
                    } else {
                        operations.forEach(op -> {
                            String catName = op.getCategoryId() != null
                                    ? categoryFacade.getCategory(op.getCategoryId()).getName()
                                    : "Без категории";
                            System.out.printf("%s | %s | %s | %s | %s%n",
                                    op.getDate().toLocalDate(),
                                    op.getType(),
                                    op.getAmount(),
                                    catName,
                                    op.getDescription());
                        });
                    }
                }
                case "3" -> printAllOperations();
                case "4" -> {
                    printAllOperations();
                    System.out.print("ID операции для удаления: ");
                    String opId = scanner.nextLine();
                    try {
                        Command command = commandFactory.createDeleteOperationCommand(opId);
                        command.execute();
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "0" -> back = true;
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void showAnalytics() {
        printAllAccounts();
        System.out.print("ID счета: ");
        String accountId = scanner.nextLine();

        LocalDateTime from = readDate("Начало периода (ГГГГ-ММ-ДД): ");
        LocalDateTime to = readDate("Конец периода (ГГГГ-ММ-ДД): ").plusDays(1).minusNanos(1);

        try {
            AnalyticsReport report = analyticsFacade.generateReport(accountId, from, to);
            report.print();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void importExportMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\nИМПОРТ/ЭКСПОРТ");
            System.out.println("1. Экспорт в JSON");
            System.out.println("2. Экспорт в YAML");
            System.out.println("3. Экспорт в CSV");
            System.out.println("4. Импорт из JSON");
            System.out.println("5. Импорт из YAML");
            System.out.println("6. Импорт из CSV");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    try {
                        exportFacade.exportToJson();
                    } catch (Exception e) {
                        System.out.println("Ошибка экспорта: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        exportFacade.exportToYaml();
                    } catch (Exception e) {
                        System.out.println("Ошибка экспорта: " + e.getMessage());
                    }
                }
                case "3" -> {
                    try {
                        exportFacade.exportToCsv();
                    } catch (Exception e) {
                        System.out.println("Ошибка экспорта: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.print("Путь к директории с JSON файлами: ");
                    String path = scanner.nextLine();
                    try {
                        jsonDataImporter.importData(path);
                    } catch (Exception e) {
                        System.out.println("Ошибка импорта: " + e.getMessage());
                    }
                }
                case "5" -> {
                    System.out.print("Путь к директории с YAML файлами: ");
                    String path = scanner.nextLine();
                    try {
                        yamlDataImporter.importData(path);
                    } catch (Exception e) {
                        System.out.println("Ошибка импорта: " + e.getMessage());
                    }
                }
                case "6" -> {
                    System.out.print("Путь к директории с CSV файлами: ");
                    String path = scanner.nextLine();
                    try {
                        csvDataImporter.importData(path);
                    } catch (Exception e) {
                        System.out.println("Ошибка импорта: " + e.getMessage());
                    }
                }
                case "0" -> back = true;
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void checkAndRecalculateBalances() {
        System.out.println("\nПРОВЕРКА БАЛАНСОВ ВСЕХ СЧЕТОВ:");
        System.out.println("===================================");

        var accounts = accountFacade.getAllAccounts();
        for (var account : accounts) {
            var operations = operationFacade.getAccountOperations(
                    account.getId(),
                    LocalDateTime.MIN,
                    LocalDateTime.MAX
            );

            BigDecimal calculated = operations.stream()
                    .map(op -> op.getType() == OperationType.INCOME ? op.getAmount() : op.getAmount().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (account.getBalance().compareTo(calculated) != 0) {
                System.out.printf("Счет '%s': баланс %s, расчетный %s (разница %s)%n",
                        account.getName(),
                        account.getBalance(),
                        calculated,
                        account.getBalance().subtract(calculated));
            } else {
                System.out.printf("Счет '%s': баланс корректен (%s)%n",
                        account.getName(),
                        account.getBalance());
            }
        }

        System.out.println("===================================\n");
        System.out.print("Хотите пересчитать все балансы? (да/нет): ");

        if (scanner.nextLine().equalsIgnoreCase("да")) {
            accounts.forEach(acc -> {
                Command command = commandFactory.createRecalculateBalanceCommand(acc.getId());
                command.execute();
            });
            System.out.println("Балансы всех счетов пересчитаны");
        }
    }

    private void printAllAccounts() {
        System.out.println("\nСПИСОК СЧЕТОВ:");
        System.out.println("==================");
        var accounts = accountFacade.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("Нет счетов.");
        } else {
            accounts.forEach(acc -> {
                System.out.printf("ID: %s | %s | Баланс: %s %s%n",
                        acc.getId(),
                        acc.getName(),
                        acc.getBalance().toPlainString(),
                        acc.getCurrency());
            });
        }
        System.out.println("==================\n");
    }

    private void printAllCategories() {
        System.out.println("\nСПИСОК КАТЕГОРИЙ:");
        System.out.println("=====================");
        var categories = categoryFacade.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Нет категорий.");
        } else {
            categories.forEach(cat -> {
                System.out.printf("ID: %s | %s | Тип: %s | %s%n",
                        cat.getId(),
                        cat.getName(),
                        cat.getType(),
                        cat.getDescription());
            });
        }
        System.out.println("=====================\n");
    }

    private void printAllOperations() {
        System.out.println("\nСПИСОК ВСЕХ ОПЕРАЦИЙ:");
        System.out.println("==========================");
        var operations = operationFacade.getAllOperations();
        if (operations.isEmpty()) {
            System.out.println("Нет операций.");
        } else {
            operations.forEach(op -> {
                String catName = op.getCategoryId() != null
                        ? categoryFacade.getCategory(op.getCategoryId()).getName()
                        : "Без категории";
                System.out.printf("ID: %s | %s | %s | %s | %s | %s%n",
                        op.getId(),
                        op.getDate().toLocalDate(),
                        op.getType(),
                        op.getAmount().toPlainString(),
                        catName,
                        op.getDescription() != null ? op.getDescription() : "");
            });
        }
        System.out.println("==========================\n");
    }

    private String readCurrency() {
        Set<String> validCurrencies = Set.of("RUB", "USD", "EUR");
        String currency;
        boolean isValid;

        do {
            System.out.print("Валюта (RUB/USD/EUR): ");
            currency = scanner.nextLine().trim().toUpperCase();
            isValid = validCurrencies.contains(currency);

            if (!isValid) {
                System.out.println("Ошибка! Введите RUB, USD или EUR.");
            }
        } while (!isValid);

        return currency;
    }

    private OperationType readOperationType() {
        OperationType type = null;
        boolean isValid = false;

        while (!isValid) {
            System.out.print("Тип (INCOME/EXPENSE): ");
            try {
                type = OperationType.valueOf(scanner.nextLine().trim().toUpperCase());
                isValid = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка! Введите INCOME или EXPENSE.");
            }
        }
        return type;
    }

    private LocalDateTime readDate(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime date = null;
        boolean isValid = false;

        while (!isValid) {
            System.out.print(prompt);
            try {
                date = LocalDateTime.parse(scanner.nextLine().trim() + "T00:00:00",
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                isValid = true;
            } catch (Exception e) {
                System.out.println("Ошибка! Введите дату в формате ГГГГ-ММ-ДД (например, 2025-02-16).");
            }
        }
        return date;
    }
}