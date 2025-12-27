public class Customer {
    private static int nextId = 1; // переменная класса
    private int id;
    private String fullName;

    public Customer(String fullName) {
        this.id = nextId++;
        this.fullName = fullName;
    }

    public int getId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }
}