public class CreditAccount extends Account{
    private double creditLimit;

    public double getCreditLimit(){
        return this.creditLimit;
    }

    CreditAccount(Customer owner, double balance, double creditLimit){
        super(owner, balance);
        this.creditLimit = creditLimit;
    }

    @Override
    public boolean withdraw(double amount){
        if ((this.balance - amount >= - this.creditLimit) && amount > 0){
            this.balance-=amount;
            return true;
        }
        return false;
    }
}