import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // 1. Создаем зоопарк
        Zoo moscowZoo = new Zoo("Московский зоопарк", "Москва");

        // 2. Создаем животных разных видов
        Lion simba = new Lion(1, "Симба", 80, 5, 190, 10.5, "Мясо", "10:00, 18:00", 4);
        Lion nala = new Lion(2, "Нала", 90, 4, 150, 8.0, "Мясо", "10:00, 18:00", 3);

        Snake kaa = new Snake(3, "Каа", 40, 10, 15, 1.0, "Мышь", "Раз в неделю", 1, 5.5);

        Parrot kesha = new Parrot(4, "Кеша", 100, 2, 1, 0.1, "Семечки", "Весь день", 2, "Кеша хороший!");

        Pike shuka = new Pike(5, "Зубастая", 70, 3, 4, 0.5, "Мальки", "Утро", 1);

        // Добавляем всех в общий список зоопарка
        moscowZoo.addAnimal(simba);
        moscowZoo.addAnimal(nala);
        moscowZoo.addAnimal(kaa);
        moscowZoo.addAnimal(kesha);
        moscowZoo.addAnimal(shuka);

        // 3. Создаем несколько вольеров
        Enclosure savanna = new Enclosure(1, "Саванна", 2, 10);
        Enclosure jungle = new Enclosure(2, "Джунгли", 5, 10);
        Enclosure aquarium = new Enclosure(3, "Аквариум", 10, 10);

        // Расселяем животных
        savanna.addAnimal(simba);
        savanna.addAnimal(nala);

        jungle.addAnimal(kaa);
        jungle.addAnimal(kesha);

        aquarium.addAnimal(shuka);

        moscowZoo.addEnclosure(savanna);
        moscowZoo.addEnclosure(jungle);
        moscowZoo.addEnclosure(aquarium);

        // 4. Нанимаем сотрудников разных специальностей
        Feeder ivan = new Feeder(101, "Иван", "Смена А");
        Veterinarian anna = new Veterinarian(102, "Анна", "Смена Б");
        Cleaner petr = new Cleaner(103, "Петр", "Утро");

        moscowZoo.addEmployee(ivan);
        moscowZoo.addEmployee(anna);
        moscowZoo.addEmployee(petr);

        // 5. Голоса животных
        System.out.println("\n--- Слушаем голоса животных ---");
        simba.getVoice(); // лев рычит
        kaa.getVoice();   // змея шипит
        kesha.getVoice(); // попугай говорит
        // у щуки нет метода getVoice в коде, поэтому её не вызываем

        // 6. Ежедневная рутина
        System.out.println("\n--- Рабочий процесс ---");
        moscowZoo.feedAllAnimals(ivan);      // Иван кормит всех
        moscowZoo.clearAllEnclosures(petr);  // Петр убирает вольеры
        moscowZoo.healAllAnimals(anna);      // Анна лечит тех, кто приболел

        // 7. Переход к следующему дню
        moscowZoo.passDay();

        // 8. Генерация отчетов
        ReportGenerator reportGen = new ReportGenerator();
        System.out.println("\n--- Итоговые отчеты ---");
        reportGen.reportZoo(moscowZoo);
        reportGen.reportAllAnimals(moscowZoo);
        reportGen.reportEnclosureStatus(moscowZoo);

        // 9. Ежедневная рутина
        System.out.println("\n--- Рабочий процесс ---");
        moscowZoo.feedAllAnimals(ivan);      // Иван кормит всех
        moscowZoo.clearAllEnclosures(petr);  // Петр убирает вольеры
        moscowZoo.healAllAnimals(anna);      // Анна лечит тех, кто приболел

        // 10. Переход к следующему дню
        moscowZoo.passDay();

        // 11. Генерация отчетов
        System.out.println("\n--- Итоговые отчеты ---");
        reportGen.reportZoo(moscowZoo);
        reportGen.reportAllAnimals(moscowZoo);
        reportGen.reportEnclosureStatus(moscowZoo);
    }
}