import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {

    public void reportZoo(ZooInfoProvider zoo) {
        System.out.println("===== ОТЧЕТ ПО ЗООПАРКУ " + zoo.getName() + " в г." + zoo.getCity() + " =====");
        System.out.println("Всего животных: " + zoo.getAnimals().size());

        // коллекция вид - кол-во
        Map<String, Long> animalCounts = zoo.getAnimals().stream()
                .collect(Collectors.groupingBy(e -> e.getClass().getSimpleName(), Collectors.counting()));
        System.out.println("Животные по видам:");
        animalCounts.forEach((kind, count) -> System.out.println(" - " + kind + ": " + count));

        Map<String, Long> employeeCounts = zoo.getEmployees().stream()
                .collect(Collectors.groupingBy(e -> e.getClass().getSimpleName(), Collectors.counting()));
        System.out.println("Сотрудники по должностям:");
        employeeCounts.forEach((role, count) -> System.out.println(" - " + role + ": " + count));

        System.out.println("Вольеров в эксплуатации: " + zoo.getEnclosures().size());
    }

    public void reportAllAnimals(AnimalInfoProvider info) {
        System.out.println("--- ОТЧЕТ ПО ЖИВОТНЫМ ---");
        for (Animal animal : info.getAnimals()) {
            System.out.println("Вид: " + animal.getClass().getSimpleName() + " | Имя: " + animal.getName() +
                    " | Здоровье: " + animal.getHealth() + " | Возраст: " + animal.getAge() +
                    " | Вес: " + animal.getWeight() + " | Еда: " + animal.getFoodAmountNeeded() +
                    " кг " + animal.getFoodName() + " | График: " + animal.getSchedule());
        }
        System.out.println("-".repeat(10));
    }

    public void reportIllAnimals(AnimalInfoProvider info) {
        System.out.println("--- ОТЧЕТ ПО БОЛЬНЫМ ЖИВОТНЫМ ---");
        boolean hasIll = false;
        for (Animal animal : info.getAnimals()) {
            if (animal.isIllness()) { // Используем метод из класса Animal
                System.out.println("Вид: " + animal.getClass().getSimpleName() + " | Имя: " + animal.getName() +
                        " | Здоровье: " + animal.getHealth());
                hasIll = true;
            }
        }
        if (!hasIll) {
            System.out.println("Все животные здоровы.");
        }
        System.out.println("-".repeat(10));
    }

    public void reportEnclosureStatus(EnclosureInfoProvider info) {
        System.out.println("--- ОТЧЕТ ПО ВОЛЬЕРАМ ---");
        for (Enclosure enclosure : info.getEnclosures()) {
            System.out.println("ID: " + enclosure.getId() + " | Вид: " + enclosure.getKind() +
                    " | Чистота: " + enclosure.getDegreeOfPurity() + "/10");
        }
        System.out.println("-".repeat(10));
    }
}