public abstract class Employee {
    private int id;
    private String name;
    private String Shedule;

    public Employee(int id, String name, String Shedule) {
        this.id = id;
        this.name = name;
        this.Shedule = Shedule;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getShedule() { return Shedule; }

    public void setShedule(String shedule) { Shedule = shedule; }
}