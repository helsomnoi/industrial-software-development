interface Cleanable{
    void clean(Enclosure enclosure);
}

public class Cleaner extends Employee implements Cleanable{
    public Cleaner(int id, String name, String schedule) {
        super(id, name, schedule);
    }

    public void clean(Enclosure enclosure){
        System.out.println("Степень чистоты вольера № " + enclosure.getId() + " :" + enclosure.getDegreeOfPurity());
        if (enclosure.getDegreeOfPurity() < 10){
            enclosure.setDegreeOfPurity(10);
            System.out.println("Вольер № " + enclosure.getId() + " полностью очищен");
        } else {
            System.out.println("Вольер уже чист!");
        }
    }
}