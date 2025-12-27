import java.util.ArrayList;

public class Bank {
    ArrayList<Customer> customers;
    ArrayList<Account> accounts;
    ArrayList<Transaction> transactions;

    public Bank(){
        customers = new ArrayList<>();
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public Customer createCustomer(String fullName){
        Customer customer = new Customer(fullName);
        customers.add(customer);
        return customer;
    }

    public Account openDebitAccount(Customer owner){
        DebitAccount account = new DebitAccount(owner, 0);
        accounts.add(account);
        return account;
    }

    public Account openCreditAccount(Customer owner, double creditLimit){
        CreditAccount account = new CreditAccount(owner, 0, creditLimit);
        accounts.add(account);
        return account;
    }

    public Account findAccount(int accountNumber){
        for (Account account : accounts){
            if (account.getAccountNumber() == accountNumber){
                return account;
            }
        }
        return null;
    }

    public boolean deposit(int accountNumber, double amount){
        Account account = findAccount(accountNumber);
        if (account == null || amount <= 0){
            String message = account == null ? "Счет не найден" : "Неверная сумма";
            Transaction transaction = new Transaction(Transaction.transactionType.DEPOSIT,
                    amount, null, accountNumber);
            transaction.setSuccess(false);
            transaction.setMessage(message);
            transactions.add(transaction);
            return false;
        }

        boolean success = account.deposit(amount);
        String message = success ? "ОК" : "Ошибка при пополнении";
        Transaction transaction = new Transaction(Transaction.transactionType.DEPOSIT,
                amount, null, accountNumber);
        transaction.setSuccess(true);
        transaction.setMessage(message);
        transactions.add(transaction);
        return success;
    }

    public boolean withdraw(int accountNumber, double amount){
        Account account = findAccount(accountNumber);
        if (account == null || amount <= 0){
            String message = account == null ? "Счет не найден" : "Неверная сумма";
            Transaction transaction = new Transaction(Transaction.transactionType.WITHDRAW,
                    amount, accountNumber, null);
            transaction.setSuccess(false);
            transaction.setMessage(message);
            transactions.add(transaction);
            return false;
        }

        boolean success = account.withdraw(amount);
        String message = success ? "ОК" : "Недостаточно средств";
        Transaction transaction = new Transaction(Transaction.transactionType.WITHDRAW,
                amount, accountNumber, null);
        transaction.setSuccess(success);
        transaction.setMessage(message);
        transactions.add(transaction);
        return success;
    }

    public boolean transfer(int fromAccountNumber, int toAccountNumber, double amount){
        Account fromAccount = findAccount(fromAccountNumber);
        Account toAccount = findAccount(toAccountNumber);

        if (fromAccount == null || toAccount == null || amount <= 0){
            String message = fromAccount == null ? "Счет отправителя не найден" :
                    (toAccount == null ? "Счет получателя не найден" : "Неверная сумма");
            Transaction transaction = new Transaction(Transaction.transactionType.TRANSFER,
                    amount, fromAccountNumber, toAccountNumber);
            transaction.setSuccess(false);
            transaction.setMessage(message);
            transactions.add(transaction);
            return false;
        }

        boolean success = fromAccount.transfer(toAccount, amount);
        String message = success ? "ОК" : "Недостаточно средств у отправителя";
        Transaction transaction = new Transaction(Transaction.transactionType.TRANSFER,
                amount, fromAccountNumber, toAccountNumber);
        transaction.setSuccess(true);
        transaction.setMessage(message);
        transactions.add(transaction);
        return success;
    }

    public void printCustomerAccounts(int customerId){
        System.out.println("Счета владельца ID: " + customerId);
        boolean hasAccount = false;

        for (Account account : this.accounts){
            if (account.getOwner().getId() == customerId){
                System.out.println("Счет №" + account.getAccountNumber() + " | Баланс " +
                        account.getBalance() + " | Тип: " + account.getClass().getSimpleName());
                hasAccount = true;
            }
        }

        if (!hasAccount){
            System.out.println("У владельца ID: " + customerId + " нет открытых счетов");
        }
        return;
    }

    public void printTransactions(){
        System.out.println("Всего было совершено" + this.transactions.size() + " транзакций");
        for (Transaction transaction : this.transactions){
            System.out.println(transaction.toString());
        }
    }

    public void printReport(){
        System.out.println("====ОТЧЕТ====");
        int debitCount = 0;
        int creditCount = 0;
        double debitTotal = 0;
        double creditTotal = 0;
        int successCount = 0;
        int failCount = 0;

        for (Account account: this.accounts){
            if (account instanceof DebitAccount){
                debitCount++;
                debitTotal += account.getBalance();
            }
            if (account instanceof CreditAccount){
                creditCount++;
                creditTotal += account.getBalance();
            }
        }

        System.out.println("Всего счетов: " + (debitCount + creditCount));
        System.out.println("Дебетовые счета: " + debitCount);
        System.out.println("Кредитные счета: " + creditCount);

        System.out.println("Общий баланс счетов: " + (debitTotal +  creditTotal));
        System.out.println("Баланс дебетовых счетов: " + debitTotal);
        System.out.println("Баланс кредитных счетов: " + creditTotal);

        for (Transaction transaction : this.transactions){
            if (transaction.isSuccess()){
                successCount++;
            } else {
                failCount++;
            }
        }
        System.out.println("Всего операций: " + (successCount + failCount));
        System.out.println("Успешных операций: " + successCount);
        System.out.println("Неуспешных операций: " + failCount);

        System.out.println("Всего клиентов: " + this.customers.size());
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    // Дополнительный метод поиска клиента по ID
    public Customer findCustomerById(int customerId) {
        for (Customer customer : customers) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null;
    }
}