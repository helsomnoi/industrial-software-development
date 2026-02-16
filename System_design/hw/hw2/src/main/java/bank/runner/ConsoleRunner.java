package bank.runner;

import bank.domain.BankAccount;
import bank.domain.Category;
import bank.domain.Operation;
import bank.domain.OperationType;
import bank.service.BankService;
import bank.service.AnalyticsService;
import bank.service.ImportExportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
@Profile("!test")
public class ConsoleRunner implements CommandLineRunner {

    private final BankService bankService;
    private final AnalyticsService analyticsService;
    private final ImportExportService importExportService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleRunner(BankService bankService,
                         AnalyticsService analyticsService,
                         ImportExportService importExportService) {
        this.bankService = bankService;
        this.analyticsService = analyticsService;
        this.importExportService = importExportService;
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
                case "6" -> {
                    bankService.checkAllBalances();
                    System.out.print("Хотите пересчитать все балансы? (да/нет): ");
                    if (scanner.nextLine().equalsIgnoreCase("да")) {
                        bankService.recalculateAllBalances();
                    }
                }
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

        bankService.createCategory("Зарплата", OperationType.INCOME, "Основной доход");
        bankService.createCategory("Кэшбэк", OperationType.INCOME, "Бонусы от банка");
        bankService.createCategory("Продукты", OperationType.EXPENSE, "Еда и напитки");
        bankService.createCategory("Транспорт", OperationType.EXPENSE, "Такси и общественный транспорт");
        bankService.createCategory("Рестораны", OperationType.EXPENSE, "Кафе и рестораны");

        BankAccount mainAccount = bankService.createAccount("Основной счет", "RUB");
        BankAccount savingsAccount = bankService.createAccount("Накопительный", "RUB");

        bankService.createOperation(
                OperationType.INCOME,
                mainAccount.getId(),
                new BigDecimal("100000"),
                "Зарплата за январь",
                bankService.getCategoriesByType(OperationType.INCOME).get(0).getId()
        );

        bankService.createOperation(
                OperationType.EXPENSE,
                mainAccount.getId(),
                new BigDecimal("15000"),
                "Продукты на месяц",
                bankService.getCategoriesByType(OperationType.EXPENSE).get(0).getId()
        );

        bankService.createOperation(
                OperationType.EXPENSE,
                mainAccount.getId(),
                new BigDecimal("5000"),
                "Такси",
                bankService.getCategoriesByType(OperationType.EXPENSE).get(1).getId()
        );

        bankService.createOperation(
                OperationType.INCOME,
                savingsAccount.getId(),
                new BigDecimal("5000"),
                "Перевод с основного",
                null
        );

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
                    try {
                        bankService.createAccount(name, currency);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "2" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String id = scanner.nextLine();
                    System.out.print("Новое название: ");
                    String newName = scanner.nextLine();
                    try {
                        bankService.updateAccount(id, newName);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "3" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String id = scanner.nextLine();
                    try {
                        bankService.deleteAccount(id);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "4" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String id = scanner.nextLine();
                    try {
                        bankService.recalculateAccountBalance(id);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
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
                    String typeStr = scanner.nextLine().toUpperCase();;
                    System.out.print("Описание: ");
                    String desc = scanner.nextLine();
                    try {
                        OperationType type = OperationType.valueOf(typeStr.toUpperCase());
                        bankService.createCategory(name, type, desc);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
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
                        bankService.updateCategory(id, newName, newDesc);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "3" -> {
                    printAllCategories();
                    System.out.print("ID категории: ");
                    String id = scanner.nextLine();
                    try {
                        bankService.deleteCategory(id);
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
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    printAllAccounts();
                    System.out.print("ID счета: ");
                    String accountId = scanner.nextLine();
                    System.out.print("Тип (INCOME/EXPENSE): ");
                    String typeStr = scanner.nextLine().toUpperCase();;
                    System.out.print("Сумма: ");
                    BigDecimal amount = new BigDecimal(scanner.nextLine());
                    System.out.print("Описание: ");
                    String desc = scanner.nextLine();
                    printAllCategories();
                    System.out.print("ID категории (Enter для пропуска): ");
                    String catId = scanner.nextLine();

                    try {
                        OperationType type = OperationType.valueOf(typeStr.toUpperCase());
                        bankService.createOperation(type, accountId, amount, desc,
                                catId.isEmpty() ? null : catId);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "2" -> {
                    System.out.print("ID счета: ");
                    String accountId = scanner.nextLine();
                    try {
                        List<bank.domain.Operation> operations = bankService.getAccountOperations(accountId,
                                LocalDateTime.now().minusMonths(1), LocalDateTime.now());
                        System.out.println("\nОПЕРАЦИИ:");
                        operations.forEach(op -> {
                            String catName = op.getCategoryId() != null
                                    ? bankService.getCategory(op.getCategoryId()).getName()
                                    : "Без категории";
                            System.out.printf("%s | %s | %s | %s%n",
                                    op.getDate().toLocalDate(),
                                    op.getType(),
                                    op.getAmount(),
                                    catName);
                        });
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
                case "3" -> {
                    printAllOperations();
                }
                case "0" -> back = true;
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void showAnalytics() {
        printAllOperations();
        System.out.print("ID счета: ");
        String accountId = scanner.nextLine();
        System.out.print("Начало периода (ГГГГ-ММ-ДД): ");
        String fromStr = scanner.nextLine();
        System.out.print("Конец периода (ГГГГ-ММ-ДД): ");
        String toStr = scanner.nextLine();

        try {
            LocalDateTime from = LocalDateTime.parse(fromStr + "T00:00:00");
            LocalDateTime to = LocalDateTime.parse(toStr + "T23:59:59");
            analyticsService.printAnalytics(accountId, from, to);
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
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    try {
                        importExportService.exportToJson();
                    } catch (Exception e) {
                        System.out.println("Ошибка экспорта: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        importExportService.exportToYaml();
                    } catch (Exception e) {
                        System.out.println("Ошибка экспорта: " + e.getMessage());
                    }
                }
                case "3" -> {
                    try {
                        importExportService.exportToCsv();
                    } catch (Exception e) {
                        System.out.println("Ошибка экспорта: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.print("Путь к директории с JSON файлами: ");
                    String path = scanner.nextLine();
                    importExportService.importFromJson(path);
                }
                case "0" -> back = true;
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void printAllAccounts() {
        System.out.println("\nСПИСОК СЧЕТОВ:");
        System.out.println("==================");
        List<BankAccount> accounts = bankService.getAllAccounts();
        accounts.forEach(acc -> {
            System.out.printf("ID: %s | %s | Баланс: %s %s | Операций: %d%n",
                    acc.getId(),
                    acc.getName(),
                    acc.getBalance().toPlainString(),
                    acc.getCurrency(),
                    acc.getOperations().size());
        });
        System.out.println("==================\n");
    }

    private void printAllCategories() {
        System.out.println("\nСПИСОК КАТЕГОРИЙ:");
        System.out.println("=====================");
        List<Category> categories = bankService.getAllCategories();
        categories.forEach(cat -> {
            System.out.printf("ID: %s | %s | Тип: %s | %s%n",
                    cat.getId(),
                    cat.getName(),
                    cat.getType(),
                    cat.getDescription());
        });
        System.out.println("=====================\n");
    }

    private void printAllOperations() {
        System.out.println("\nСПИСОК ОПЕРАЦИЙ:");
        System.out.println("==========================");
        List<Operation> operations = bankService.getAllOperations();
        if (operations.isEmpty()) {
            System.out.println("Нет операций.");
        } else {
            operations.forEach(op -> {
                String catName = op.getCategoryId() != null
                        ? bankService.getCategory(op.getCategoryId()).getName()
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

}