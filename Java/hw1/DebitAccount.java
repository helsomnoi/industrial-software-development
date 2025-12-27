public class DebitAccount extends Account{

    DebitAccount(Customer owner, double balance){
        super(owner,balance);
    }

    @Override
    public boolean withdraw(double amount){
        if (this.balance >= amount && amount > 0){
            this.balance-=amount;
            return true;
        }
        return false;
    }
}