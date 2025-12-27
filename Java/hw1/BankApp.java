import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.ArrayList;

public class BankApp {
    private static Bank bank = new Bank();
    private static Scanner scanner = new Scanner(System.in); // Единое имя scanner

    public static void main(String[] args) {
        showMainMenu();
    }

    public static void showMainMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("=== БАНКОВСКАЯ СИСТЕМА ===");
            System.out.println("1. Создать клиента");
            System.out.println("2. Открыть дебетовый счет");
            System.out.println("3. Открыть кредитный счет");
            System.out.println("4. Пополнить счет");
            System.out.println("5. Снять деньги со счета");
            System.out.println("6. Перевести деньги между счетами");
            System.out.println("7. Показать счета клиента");
            System.out.println("8. Показать транзакции");
            System.out.println("9. Отчет банка");
            System.out.println("10. Выход");

            try {
                System.out.print("Выберите пункт меню: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        createCustomer();
                        break;
                    case 2:
                        openDebitAccount();
                        break;
                    case 3:
                        openCreditAccount();
                        break;
                    case 4:
                        depositToAccount();
                        break;
                    case 5:
                        withdrawFromAccount(); // Исправлено имя
                        break;
                    case 6:
                        transferBetweenAccounts();
                        break;
                    case 7:
                        showCustomerAccounts();
                        break;
                    case 8:
                        showTransactions();
                        break;
                    case 9:
                        showBankReport();
                        break;
                    case 10:
                        exit = true;
                        System.out.println("Выход из системы");
                        break;
                    default:
                        System.out.println("Неверный ввод. Введите число от 1 до 10");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите число от 1 до 10");
                scanner.nextLine();
            }
            // НЕ закрывать scanner здесь!
        }
        // scanner.close(); // Можно закрыть только при выходе из программы
    }

    private static void createCustomer() {
        System.out.print("Создание клиента. Введите ФИО: ");
        String fullName = scanner.nextLine();
        if (fullName.trim().equals("")) {
            System.out.println("Ошибка: ФИО не может быть пустым");
            return;
        }

        Customer customer = bank.createCustomer(fullName);
        System.out.println("Клиент успешно создан");
        System.out.println("ID: " + customer.getId() + " | ФИО: " + customer.getFullName());
    }

    private static void openDebitAccount() {
        System.out.println("Открытие дебетового счета. Необходимо выбрать клиента");
        Customer customer = selectCustomer();
        if (customer == null) return;

        Account account = bank.openDebitAccount(customer);
        System.out.println("Дебетовый счет успешно открыт");
        System.out.println("Номер счета: " + account.getAccountNumber() +
                " | Владелец: " + account.getOwner().getFullName());
    }

    private static void openCreditAccount() {
        System.out.println("Открытие кредитного счета. Необходимо выбрать клиента");
        Customer customer = selectCustomer();
        if (customer == null) return;

        System.out.print("Введите кредитный лимит: ");
        try {
            double creditLimit = scanner.nextDouble();
            scanner.nextLine();

            if (creditLimit <= 0) {
                System.out.println("Ошибка: кредитный лимит должен быть больше 0");
                return;
            }
            Account account = bank.openCreditAccount(customer, creditLimit);
            System.out.println("Кредитный счет успешно открыт");
            System.out.println("Номер счета: " + account.getAccountNumber() +
                    " | Владелец: " + account.getOwner().getFullName() +
                    " | Кредитный лимит: " + creditLimit);
        } catch(InputMismatchException e) {
            System.out.println("Ошибка: введите корректный кредитный лимит");
            scanner.nextLine();
        }
    }

    private static void depositToAccount() {
        System.out.println("Пополнение счета");
        int accountNumber = selectAccountNumber();
        if (accountNumber == -1) return;

        System.out.print("Введите сумму для пополнения: ");
        try {
            String amountInput = scanner.nextLine().trim();
            double amount = Double.parseDouble(amountInput);
            if (amount <= 0) {
                System.out.println("Ошибка: сумма должна быть больше 0");
                return;
            }

            boolean success = bank.deposit(accountNumber, amount);
            if (success) {
                System.out.println("Счет успешно пополнен на " + amount);
                Account account = bank.findAccount(accountNumber);
                if (account != null) {
                    System.out.println("Текущий баланс: " + account.getBalance());
                }
            } else {
                ArrayList<Transaction> transactions = bank.getTransactions();
                if (!transactions.isEmpty()) {
                    Transaction lastTransaction = transactions.get(transactions.size() - 1);
                    System.out.println("Ошибка при пополнении счета: " + lastTransaction.getMessage());
                } else {
                    System.out.println("Ошибка при пополнении счета");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректную сумму");
            scanner.nextLine();
        }
    }

    private static void withdrawFromAccount() {
        System.out.println("Снятие со счета");
        int accountNumber = selectAccountNumber();
        if (accountNumber == -1) return;

        System.out.print("Введите сумму для снятия: ");
        try {
            String amountInput = scanner.nextLine().trim();
            double amount = Double.parseDouble(amountInput);

            if (amount <= 0) {
                System.out.println("Ошибка: сумма должна быть больше 0");
                return;
            }

            boolean success = bank.withdraw(accountNumber, amount);
            if (success) {
                System.out.println("Со счета успешно снято " + amount);
                Account account = bank.findAccount(accountNumber);
                if (account != null) {
                    System.out.println("Текущий баланс: " + account.getBalance());
                }
            } else {
                ArrayList<Transaction> transactions = bank.getTransactions();
                if (!transactions.isEmpty()) {
                    Transaction lastTransaction = transactions.get(transactions.size() - 1);
                    System.out.println("Ошибка при снятии со счета: " + lastTransaction.getMessage());
                } else {
                    System.out.println("Ошибка при снятии со счета");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректную сумму");
            scanner.nextLine();
        }
    }

    private static void transferBetweenAccounts() {
        System.out.println("Перевод между счетами");

        System.out.println("Счет отправителя:");
        int fromAccount = selectAccountNumber();
        if (fromAccount == -1) return;

        System.out.println("Счет получателя:");
        int toAccount = selectAccountNumber();
        if (toAccount == -1) return;

        if (fromAccount == toAccount) {
            System.out.println("Ошибка: нельзя перевести на тот же счёт!");
            return;
        }

        System.out.print("Введите сумму для перевода: ");
        try {
            String  amountInput = scanner.nextLine().trim();
            double amount = Double.parseDouble(amountInput);

            if (amount <= 0) {
                System.out.println("Ошибка: сумма должна быть больше 0");
                return;
            }

            boolean success = bank.transfer(fromAccount, toAccount, amount);
            if (success) {
                System.out.println("Успешно выполнен перевод на сумму " + amount);
            } else {
                ArrayList<Transaction> transactions = bank.getTransactions();
                if (!transactions.isEmpty()) {
                    Transaction lastTransaction = transactions.get(transactions.size() - 1);
                    System.out.println("Ошибка при переводе: " + lastTransaction.getMessage());
                } else {
                    System.out.println("Ошибка при переводе");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректную сумму");
            scanner.nextLine();
        }
    }

    private static void showCustomerAccounts() {
        System.out.println("Счета клиента:");
        Customer customer = selectCustomer();
        if (customer == null) return;

        bank.printCustomerAccounts(customer.getId());
    }

    private static void showTransactions() {
        bank.printTransactions();
    }

    private static void showBankReport() {
        bank.printReport();
    }

    private static Customer selectCustomer() {
        if (bank.getCustomers().isEmpty()) {
            System.out.println("В системе нет клиентов!");
            return null;
        }

        System.out.println("Список клиентов:");
        for (Customer customer : bank.getCustomers()) {
            System.out.println(customer.getId() + ". " + customer.getFullName());
        }

        System.out.print("Введите ID клиента: ");
        try {
            int customerId = scanner.nextInt();
            scanner.nextLine();

            Customer customer = bank.findCustomerById(customerId);
            if (customer == null) {
                System.out.println("Клиент с ID " + customerId + " не найден!");
                return null;
            }
            return customer;
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректный ID!");
            scanner.nextLine();
            return null;
        }
    }

    private static int selectAccountNumber() {
        ArrayList<Account> accounts = bank.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("В банковской системе нет открытых счетов");
            return -1;
        }

        System.out.println("Список счетов:");
        for (Account account : accounts) {
            Customer owner = account.getOwner();
            System.out.println(account.getAccountNumber() + " | " +
                    owner.getFullName() + " | Баланс: " + account.getBalance() +
                    " | Тип: " + account.getClass().getSimpleName());
        }

        System.out.print("Введите номер счета: ");
        try {
            int accountNumber = scanner.nextInt();
            scanner.nextLine();

            Account account = bank.findAccount(accountNumber);
            if (account == null) {
                System.out.println("Счет с номером " + accountNumber + " не найден");
                return -1;
            }
            return accountNumber;
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректный номер счета!");
            scanner.nextLine();
            return -1;
        }
    }
}