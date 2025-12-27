public class Account{
    private static int nextAccNumber = 1000;    // пусть номера аккаунтов начинаются с 1000
    private int accountNumber;
    protected double balance;
    private Customer owner;

    public Account(Customer owner, double balance){
        this.accountNumber = nextAccNumber++;
        this.owner=owner;
        this.balance=balance;
    }

    public double getBalance(){
        return this.balance;
    }

    public int getAccountNumber(){
        return this.accountNumber;
    }

    public Customer getOwner(){
        return this.owner;
    }

    public boolean deposit(double amount){
        if (amount > 0){
            this.balance+=amount;
            return true;
        }
        return false;
    }

    public boolean withdraw(double amount){
        if (this.balance >= amount){
            this.balance-=amount;
            return true;
        }
        return false;
    }

    public boolean transfer(Account to, double amount){
        if (this.withdraw(amount)) { // валидация и списания
            to.deposit(amount);      // зачисление
            return true;
        }
        return false;
    }
}