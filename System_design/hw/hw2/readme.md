Скринкаст есть в папке hw2, а также по сслыке https://disk.yandex.ru/i/Hn-od_-UFa-xOA

Программа моделирует упрощённую банковскую систему для ведения личных финансов. Она позволяет пользователю управлять банковскими счетами, категориями доходов и расходов, а также операциями по счетам. Основные сущности:

**BankAccount** - содержит уникальный идентификатор, название, текущий баланс, валюту, даты создания и обновления, а также список операций по счёту. Поддерживает методы пополнения (deposit), снятия (withdraw) и пересчёта баланса (recalculateBalance).

**Category** — группирует операции по типу (доход или расход) и имеет название и описание.

**Operation** — фиксирует движение средств: тип (доход/расход), сумму, дату, описание, привязку к счёту и опционально к категории.

# Функциональность:

Создание, редактирование, удаление счетов и категорий;

Добавление операций (доходов/расходов) с автоматическим обновлением баланса счёта;

Аналитика за период: подсчёт доходов, расходов, разницы, группировка по категориям;

Импорт/экспорт данных в форматах JSON, YAML, CSV;

Автоматический и ручной пересчёт баланса при обнаружении несоответствий;

Измерение времени выполнения пользовательских сценариев.

# codesytle
В проекте соблюдаются общепринятые соглашения Java и единый стиль кодирования:

Именование классов - PascalCase (BankAccount, AnalyticsService);

Именование методов и переменных - camelCase (createAccount, accountBalance);

Константы — UPPER_SNAKE_CASE (MAX_TRANSACTION_AMOUNT);

Пакеты — нижний регистр, иерархия отражает модули: bank.domain, bank.service, bank.runner;

Форматирование — используется стандартный отступ в 4 пробела, фигурные скобки открываются на той же строке (K&R стиль).

Lombok — применяется для сокращения шаблонного кода: аннотации @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor для моделей; @Slf4j для логирования.

Пример:

```java
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccount {
    private String id;
    private String name;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```
Код отформатирован с помощью встроенных средств IntelliJ IDEA, что обеспечивает единообразие.

# Модульное тестирование
Проект покрыт модульными тестами с использованием JUnit 5 и Mockito. Тесты расположены в директории src/test/java.

Используемые инструменты:

JUnit 5 - основа для написания тестов (аннотации @Test, @BeforeEach, @ExtendWith).

Mockito - создание заглушек для зависимостей (@Mock, @InjectMocks, when, verify).

JaCoCo - отчет о покрытии кода тестами

Структура тестов:

bank.domain - тесты моделей, проверяющие бизнес-логику сущностей;

bank.service - тесты сервисов, проверяющие корректность взаимодействия компонентов и обработку граничных случаев;

bank;repository;inmemory - тесты in-memory реализаций репозиториев;

bank.config - проверка наличия и корректности бинов конфигурации;

bank.runner - тест для проверки загрузки контекста (консоль не запускается в тестах);

Покрытие кода (на момент финальной версии):

bank.domain - 98% инструкций, 92% ветвлений.

bank.service - 95% инструкций, 90% ветвлений.

bank.repository.inmemory - 95% инструкций, 100% ветвлений.

bank.config - 100%.

bank.runner - 0% (консольный интерфейс, не тестируется автоматически).

bank.BankApplication - 37% (main-класс).

Общее покрытие: 63% инструкций, 61% ветвлений.

[index.html](build/reports/jacoco/test/html/index.html) - файл отчета расположен ../hw2/build/reports/jacoco/test/html/index.html
![img_1.png](src/main/img_1.png)

Для исключения сгенерированного Lombok кода из отчёта добавлен файл lombok.config:

properties
lombok.addLombokGeneratedAnnotation = true
Пример теста:

```java
@Test
void createAccount_Success() {
    when(accountRepository.existsByName("Основной")).thenReturn(false);
    when(accountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

    BankAccount account = bankService.createAccount("Основной", "RUB");

    assertNotNull(account);
    assertEquals("Основной", account.getName());
    assertEquals(BigDecimal.ZERO, account.getBalance());
    verify(accountRepository).save(account);
}
```

# Принципы SOLID
В проекте применяются принципы SOLID. Рассмотрим каждый на примерах.

**Single Responsibility Principle**
Каждый класс имеет одну чётко определённую зону ответственности:

BankAccount, Category, Operation - структура хранения данных и базовая логика;

AccountRepository, CategoryRepository, OperationRepository - только операции с хранилищем;

BankService - бизнес-логика управления счетами, категориями и операциями;

AnalyticsService - аналитические отчёты;

ImportExportService - импорт/экспорт данных;

AppConfig - конфигурация бинов;

**Open/Closed Principle** 
Классы открыты для расширения, но закрыты для модификации. Благодаря использованию интерфейсов репозиториев можно легко добавить новую реализацию (например, файловую или JPA), не меняя существующие сервисы.

**Liskov Substitution Principle** 
Все реализации интерфейсов репозиториев (InMemoryAccountRepository, FileAccountRepository и т.д.) взаимозаменяемы - они соблюдают контракты, заданные интерфейсами.

**Interface Segregation Principle** (Принцип разделения интерфейсов)
Интерфейсы репозиториев (AccountRepository, CategoryRepository, OperationRepository) содержат только методы, необходимые для работы с конкретной сущностью. Например, CategoryRepository не имеет методов для работы со счетами. Это позволяет избежать «жирных» интерфейсов.

**Dependency Inversion Principle**
Модули верхнего уровня (BankService, AnalyticsService) зависят от абстракций (интерфейсов репозиториев), а не от конкретных реализаций. Зависимости внедряются через конструктор (DI).

# Реализация взаимодействия модулей через DI-контейнер
В качестве DI-контейнера используется Spring Framework (Spring Boot). Все сервисы и репозитории являются Spring-бинами, управляемыми контейнером.

Конфигурация:

В build.gradle.kts подключён стартер spring-boot-starter.

Аннотации @Service, @Repository помечают классы как кандидаты для автоматического обнаружения.

Главный класс BankApplication аннотирован @SpringBootApplication, что включает сканирование пакетов.

Внедрение зависимостей через конструктор:

```java
@Service
public class BankService {
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final OperationRepository operationRepository;

    public BankService(AccountRepository accountRepository,
                       CategoryRepository categoryRepository,
                       OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }
}
```
Конфигурация бинов Jackson:

```java
@Configuration
public class AppConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    @Bean
    public YAMLMapper yamlMapper() {
        YAMLMapper mapper = new YAMLMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```
Тестирование с профилями:
Для предотвращения запуска ConsoleRunner во время тестов используется профиль test:

```java
@Component
@Profile("!test")
public class ConsoleRunner implements CommandLineRunner { ... }
```
В соответствующих тестовых классах добавлена аннотация @ActiveProfiles("test").

## Ситуации, при которых появятся проблемы добавления нового функционала
Несмотря на применение SOLID и DI, в текущей архитектуре есть потенциальные узкие места.

- Добавление нового типа сущности (например, «Кредитный договор»).
Потребуется создание нового репозитория, сервиса и, возможно, модификация существующих сервисов, если они должны работать с новой сущностью. Это может привести к дублированию кода;

- Изменение способа хранения данных. Сейчас данные хранятся в памяти. Переход на базу данных потребует реализации новых репозиториев, но бизнес-логика останется неизменной благодаря интерфейсам;

- Добавление нового типа отчёта в аналитику. Требует добавления метода в AnalyticsService, что пока допустимо. При большом количестве отчётов класс может разрастись; потребуется выделение отдельных классов под каждый отчёт;

- Расширение правил валидации. Валидация встроена в методы сервисов. Добавление сложных правил приведёт к усложнению кода. Решение - вынести валидацию в отдельные классы-валидаторы.

# Аргументы, почему введённые абстракции улучшили качество дизайна
Интерфейсы репозиториев - отделяют логику хранения от бизнес-логики, позволяют легко менять способ хранения и тестировать сервисы изолированно.

Разделение сервисов по функциональности (BankService, AnalyticsService, ImportExportService) - каждый сервис отвечает за свою область, что упрощает понимание и поддержку кода.

Внедрение зависимостей через конструктор - обеспечивает слабую связанность, явно показывает необходимые зависимости и упрощает тестирование.

Использование Lombok - сокращает шаблонный код, уменьшает вероятность ошибок и улучшает читаемость.

Конфигурация через @Profile - предотвращает нежелательный запуск консольного раннера в тестах, что повышает стабильность тестового окружения.

# Реализация работы с данными из файлов
Класс ImportExportService реализует импорт/экспорт данных в трёх форматах: JSON, YAML, CSV.

Используемые библиотеки:

Jackson - для JSON и YAML.

OpenCSV - для CSV.

Методы экспорта:

exportToJson(), exportToYaml(), exportToCsv() - создают в директории ./exports подпапку с временной меткой, куда сохраняются файлы accounts.*, categories.*, operations.*.

Метод импорта:

importFromJson(String importPath) - читает JSON-файлы из указанной директории, восстанавливает объекты и сохраняет их через репозитории.

Пример экспорта в JSON:
```java
public void exportToJson() {
    try {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path exportDir = Paths.get(exportPath, "json_" + timestamp);
        Files.createDirectories(exportDir);

        jsonMapper.writeValue(new File(exportDir.toString(), "accounts.json"), accountRepository.findAll());
        jsonMapper.writeValue(new File(exportDir.toString(), "categories.json"), categoryRepository.findAll());
        jsonMapper.writeValue(new File(exportDir.toString(), "operations.json"), operationRepository.findAll());

        System.out.println("Данные экспортированы в JSON: " + exportDir);
    } catch (IOException e) {
        throw new RuntimeException("Ошибка при экспорте в JSON: " + e.getMessage(), e);
    }
}
```
Пример импорта из JSON:

```java
public void importFromJson(String importPath) {
    try {
        Path importDir = Paths.get(importPath);
        BankAccount[] accounts = jsonMapper.readValue(new File(importDir.toString(), "accounts.json"), BankAccount[].class);
        for (BankAccount acc : accounts) {
            accountRepository.save(acc);
        }
    } catch (IOException e) {
        throw new RuntimeException("Ошибка при импорте из JSON", e);
    }
}
```

## Проверка  балансов
В системе реализован механизм контроля целостности данных - автоматическая и ручная проверка соответствия баланса счета сумме всех операций по нему. 
Эта функция позволяет обнаруживать и исправлять расхождения, которые могут возникнуть из-за ошибок в логике, ручного вмешательства или других причин.

Баланс счета должен всегда равняться сумме всех доходов минус сумма всех расходов. Однако в следующих ситуациях может возникнуть расхождение:

- сбой при выполнении операции - если после создания операции баланс не обновился (например, из-за исключения);

- ручное изменение баланса - если администратор или тестовый код напрямую изменил поле balance;

- импорт данных - при импорте из файлов могут загрузиться некорректные данные;

- ошибки в бизнес-логике - например, если операция была применена, но не добавлена в список операций счета.

Метод recalculateBalance() - ручной пересчет
Этот метод вычисляет ожидаемый баланс на основе списка операций и сравнивает его с текущим. Если есть расхождение, баланс корректируется, и выводится предупреждение.

```java
public void recalculateBalance() {
    BigDecimal calculatedBalance = operations.stream()
            .map(op -> {
                if (op.getType() == OperationType.INCOME) {
                    return op.getAmount();
                } else {
                    return op.getAmount().negate();
                }
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_EVEN);

    if (this.balance.compareTo(calculatedBalance) != 0) {
        System.out.println("ВНИМАНИЕ: Обнаружено несоответствие баланса!");
        System.out.println("Текущий баланс: " + this.balance);
        System.out.println("Расчетный баланс: " + calculatedBalance);
        System.out.println("Разница: " + this.balance.subtract(calculatedBalance));

        this.balance = calculatedBalance;
        this.updatedAt = LocalDateTime.now();

        System.out.println("Баланс скорректирован до: " + this.balance);
    } else {
        System.out.println("Баланс корректен: " + this.balance);
    }
}
```

Метод autoRecalculateIfNeeded() - автоматический пересчет
Этот метод вызывается автоматически после каждой операции. Если расхождение превышает 0.01 (минимальная допустимая погрешность), баланс корректируется.

```java
public boolean autoRecalculateIfNeeded() {
    BigDecimal calculatedBalance = operations.stream()
            .map(op -> {
                if (op.getType() == OperationType.INCOME) {
                    return op.getAmount();
                } else {
                    return op.getAmount().negate();
                }
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_EVEN);

    if (this.balance.subtract(calculatedBalance).abs().compareTo(new BigDecimal("0.01")) > 0) {
        System.out.println("Автоматический пересчет баланса для счета " + name);
        System.out.println("Было: " + this.balance + ", стало: " + calculatedBalance);
        this.balance = calculatedBalance;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    return false;
}
```

Так же есть метод checkAllBalances() - проверка всех балансов:
```java
public void checkAllBalances() {
    List<BankAccount> accounts = accountRepository.findAll();
    System.out.println("\nПРОВЕРКА БАЛАНСОВ ВСЕХ СЧЕТОВ:");
    System.out.println("===================================");
    for (BankAccount account : accounts) {
        BigDecimal calculated = operationRepository.sumByAccountAndTypeAndDateBetween(
                        account.getId(), OperationType.INCOME, LocalDateTime.MIN, LocalDateTime.MAX)
                .subtract(operationRepository.sumByAccountAndTypeAndDateBetween(
                        account.getId(), OperationType.EXPENSE, LocalDateTime.MIN, LocalDateTime.MAX));
        if (account.getBalance().compareTo(calculated) != 0) {
            System.out.printf("Счет '%s': баланс %s, расчетный %s (разница %s)%n",
                    account.getName(), account.getBalance(), calculated,
                    account.getBalance().subtract(calculated));
        } else {
            System.out.printf("Счет '%s': баланс корректен (%s)%n",
                    account.getName(), account.getBalance());
        }
    }
    System.out.println("===================================\n");
}
```

### Дополнительная информация:

Для запуска приложения используйте ./gradlew --console=plain bootRun.

Тесты запускаются командой ./gradlew test, отчёт о покрытии JaCoCo доступен в build/reports/jacoco/test/html/index.html.

В случае проблем с языков в консоли:
chcp 65001
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8


Результат работы программы:
PS C:\Lena\учеба\uni\Индустриальная разработка ПО (Бюро)\System Design\hw\hw2> ./gradlew --console=plain bootRun
> Task :compileJava UP-TO-DATE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE
> Task :resolveMainClassName UP-TO-DATE

> Task :bootRun

.   ____          _            __ _ _
/\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
'  |____| .__|_| |_|_| |_\__, | / / / /
=========|_|==============|___/=/_/_/_/
:: Spring Boot ::                (v3.2.0)

2026-02-16T11:26:54.195+03:00  INFO 20848 --- [           main] bank.BankApplication                     : Starting BankApplication using Java 17.0.12 with PID 20848 (C:\Lena\учеба\uni\�?ндустриальная разработка ПО (Бюро)\System Design\hw\hw2\build\classes\java\main started by R2 in C:\Lena\учеба\uni\�?ндустриальная разработка ПО (Бюро)\System Design\hw\hw2)
2026-02-16T11:26:54.197+03:00  INFO 20848 --- [           main] bank.BankApplication                     : No active profile set, falling back to 1 default profile: "default"
2026-02-16T11:26:54.785+03:00  INFO 20848 --- [           main] bank.BankApplication                     : Started BankApplication in 0.899 seconds (process running for 1.215)

БАНКОВСКАЯ С�?СТЕМА УПРАВЛЕН�?Я СЧЕТАМ�?
===========================================

Создание тестовых данных...

СП�?СОК СЧЕТОВ:
==================
ID: 52297fa7-ed51-4978-8f57-b8c8dd24e592 | Основной счет | Баланс: 80000.00 RUB | Операций: 3
ID: bbb526d0-4d15-4489-aebe-f2deaa1aec18 | Накопительный | Баланс: 5000.00 RUB | Операций: 1
==================


СП�?СОК КАТЕГОР�?Й:
=====================
ID: 6857a40e-b0c3-4eb3-92e6-64415e2037eb | Зарплата | Тип: INCOME | Основной доход
ID: 0a9c7e2a-b56a-44df-b011-bee9feb06d26 | Кэшбэк | Тип: INCOME | Бонусы от банка
ID: fb039c24-c7fb-4da1-b602-575975855b87 | Транспорт | Тип: EXPENSE | Такси и общественный транспорт
ID: 1396f535-439d-4ce0-b4c9-9934e25e9be2 | Рестораны | Тип: EXPENSE | Кафе и рестораны
ID: 7596144a-3bef-4c7b-9d86-2e7d51ca6509 | Продукты | Тип: EXPENSE | Еда и напитки
=====================


СП�?СОК ОПЕРАЦ�?Й:
==========================
ID: 309c32fb-d3e2-4189-b521-c8a4a30413a9 | 2026-02-16 | INCOME | 100000.00 | Зарплата | Зарплата за январь
ID: 161ba9e9-19b5-4a79-a7d5-a06e6ed34cfb | 2026-02-16 | EXPENSE | 15000.00 | Транспорт | Продукты на месяц
ID: 4a197e02-7b99-4b3f-807e-111543e351d0 | 2026-02-16 | INCOME | 5000.00 | Без категории | Перевод с основного
ID: 48ec94a6-5652-43cd-a7b9-81cf17ecfc25 | 2026-02-16 | EXPENSE | 5000.00 | Рестораны | Такси
==========================

Тестовые данные созданы


ГЛАВНОЕ МЕНЮ
1. Управление счетами
2. Управление категориями
3. Управление операциями
4. Аналитика
5. �?мпорт/Экспорт
6. Проверка и пересчет балансов
0. Выход
   Выберите пункт: 5

�?МПОРТ/ЭКСПОРТ
1. Экспорт в JSON
2. Экспорт в YAML
3. Экспорт в CSV
4. �?мпорт из JSON
0. Назад
   Выберите пункт: 1
   Данные экспортированы в JSON: .\exports\json_20260216_112700

�?МПОРТ/ЭКСПОРТ
1. Экспорт в JSON
2. Экспорт в YAML
3. Экспорт в CSV
4. �?мпорт из JSON
0. Назад
   Выберите пункт: 2
   Данные экспортированы в YAML: .\exports\yaml_20260216_112703

�?МПОРТ/ЭКСПОРТ
1. Экспорт в JSON
2. Экспорт в YAML
3. Экспорт в CSV
4. �?мпорт из JSON
0. Назад
   Выберите пункт: 3
   Данные экспортированы в CSV: .\exports\csv_20260216_112705

�?МПОРТ/ЭКСПОРТ
1. Экспорт в JSON
2. Экспорт в YAML
3. Экспорт в CSV
4. �?мпорт из JSON
0. Назад
   Выберите пункт: 0

ГЛАВНОЕ МЕНЮ
1. Управление счетами
2. Управление категориями
3. Управление операциями
4. Аналитика
5. �?мпорт/Экспорт
6. Проверка и пересчет балансов
0. Выход
   Выберите пункт: 1

УПРАВЛЕН�?Е СЧЕТАМ�?

СП�?СОК СЧЕТОВ:
==================
ID: 52297fa7-ed51-4978-8f57-b8c8dd24e592 | Основной счет | Баланс: 80000.00 RUB | Операций: 3
ID: bbb526d0-4d15-4489-aebe-f2deaa1aec18 | Накопительный | Баланс: 5000.00 RUB | Операций: 1
==================

1. Создать счет
2. Редактировать счет
3. Удалить счет
4. Пересчитать баланс счета
0. Назад
   Выберите пункт: 1
   Название счета: Тестовый
   Валюта (RUB/USD/EUR): RUB

УПРАВЛЕН�?Е СЧЕТАМ�?

СП�?СОК СЧЕТОВ:
==================
ID: 8e9d518d-43ee-4695-8839-e1762ca40139 | Тестовый | Баланс: 0 RUB | Операций: 0
ID: 52297fa7-ed51-4978-8f57-b8c8dd24e592 | Основной счет | Баланс: 80000.00 RUB | Операций: 3
ID: bbb526d0-4d15-4489-aebe-f2deaa1aec18 | Накопительный | Баланс: 5000.00 RUB | Операций: 1
==================

1. Создать счет
2. Редактировать счет
3. Удалить счет
4. Пересчитать баланс счета
0. Назад
   Выберите пункт: 2

СП�?СОК СЧЕТОВ:
==================
ID: 8e9d518d-43ee-4695-8839-e1762ca40139 | Тестовый | Баланс: 0 RUB | Операций: 0
ID: 52297fa7-ed51-4978-8f57-b8c8dd24e592 | Основной счет | Баланс: 80000.00 RUB | Операций: 3
ID: bbb526d0-4d15-4489-aebe-f2deaa1aec18 | Накопительный | Баланс: 5000.00 RUB | Операций: 1
==================

ID счета: 8e9d518d-43ee-4695-8839-e1762ca40139
Новое название: Демо

УПРАВЛЕН�?Е СЧЕТАМ�?

СП�?СОК СЧЕТОВ:
==================
ID: 8e9d518d-43ee-4695-8839-e1762ca40139 | Демо | Баланс: 0 RUB | Операций: 0
ID: 52297fa7-ed51-4978-8f57-b8c8dd24e592 | Основной счет | Баланс: 80000.00 RUB | Операций: 3
ID: bbb526d0-4d15-4489-aebe-f2deaa1aec18 | Накопительный | Баланс: 5000.00 RUB | Операций: 1
==================

1. Создать счет
2. Редактировать счет
3. Удалить счет
4. Пересчитать баланс счета
0. Назад
   Выберите пункт: 0

ГЛАВНОЕ МЕНЮ
1. Управление счетами
2. Управление категориями
3. Управление операциями
4. Аналитика
5. �?мпорт/Экспорт
6. Проверка и пересчет балансов
0. Выход
   Выберите пункт: 2

УПРАВЛЕН�?Е КАТЕГОР�?ЯМ�?

СП�?СОК КАТЕГОР�?Й:
=====================
ID: 6857a40e-b0c3-4eb3-92e6-64415e2037eb | Зарплата | Тип: INCOME | Основной доход
ID: 0a9c7e2a-b56a-44df-b011-bee9feb06d26 | Кэшбэк | Тип: INCOME | Бонусы от банка
ID: fb039c24-c7fb-4da1-b602-575975855b87 | Транспорт | Тип: EXPENSE | Такси и общественный транспорт
ID: 1396f535-439d-4ce0-b4c9-9934e25e9be2 | Рестораны | Тип: EXPENSE | Кафе и рестораны
ID: 7596144a-3bef-4c7b-9d86-2e7d51ca6509 | Продукты | Тип: EXPENSE | Еда и напитки
=====================

1. Создать категорию
2. Редактировать категорию
3. Удалить категорию
0. Назад
   Выберите пункт: 1
   Название категории: Хобби
   Тип (INCOME/EXPENSE): INCOME
   Описание: Перепродажа

УПРАВЛЕН�?Е КАТЕГОР�?ЯМ�?

СП�?СОК КАТЕГОР�?Й:
=====================
ID: 6857a40e-b0c3-4eb3-92e6-64415e2037eb | Зарплата | Тип: INCOME | Основной доход
ID: b3816193-fb61-4ea8-8a38-f0fad1471c29 | Хобби | Тип: INCOME | Перепродажа
ID: 0a9c7e2a-b56a-44df-b011-bee9feb06d26 | Кэшбэк | Тип: INCOME | Бонусы от банка
ID: fb039c24-c7fb-4da1-b602-575975855b87 | Транспорт | Тип: EXPENSE | Такси и общественный транспорт
ID: 1396f535-439d-4ce0-b4c9-9934e25e9be2 | Рестораны | Тип: EXPENSE | Кафе и рестораны
ID: 7596144a-3bef-4c7b-9d86-2e7d51ca6509 | Продукты | Тип: EXPENSE | Еда и напитки
=====================

1. Создать категорию
2. Редактировать категорию
3. Удалить категорию
0. Назад
   Выберите пункт: 2

СП�?СОК КАТЕГОР�?Й:
=====================
ID: 6857a40e-b0c3-4eb3-92e6-64415e2037eb | Зарплата | Тип: INCOME | Основной доход
ID: b3816193-fb61-4ea8-8a38-f0fad1471c29 | Хобби | Тип: INCOME | Перепродажа
ID: 0a9c7e2a-b56a-44df-b011-bee9feb06d26 | Кэшбэк | Тип: INCOME | Бонусы от банка
ID: fb039c24-c7fb-4da1-b602-575975855b87 | Транспорт | Тип: EXPENSE | Такси и общественный транспорт
ID: 1396f535-439d-4ce0-b4c9-9934e25e9be2 | Рестораны | Тип: EXPENSE | Кафе и рестораны
ID: 7596144a-3bef-4c7b-9d86-2e7d51ca6509 | Продукты | Тип: EXPENSE | Еда и напитки
=====================

ID категории: b3816193-fb61-4ea8-8a38-f0fad1471c29
Новое название: Ресейл
Новое описание: Перепродажа старых вещей

УПРАВЛЕН�?Е КАТЕГОР�?ЯМ�?

СП�?СОК КАТЕГОР�?Й:
=====================
ID: 6857a40e-b0c3-4eb3-92e6-64415e2037eb | Зарплата | Тип: INCOME | Основной доход
ID: b3816193-fb61-4ea8-8a38-f0fad1471c29 | Ресейл | Тип: INCOME | Перепродажа старых вещей
ID: 0a9c7e2a-b56a-44df-b011-bee9feb06d26 | Кэшбэк | Тип: INCOME | Бонусы от банка
ID: fb039c24-c7fb-4da1-b602-575975855b87 | Транспорт | Тип: EXPENSE | Такси и общественный транспорт
ID: 1396f535-439d-4ce0-b4c9-9934e25e9be2 | Рестораны | Тип: EXPENSE | Кафе и рестораны
ID: 7596144a-3bef-4c7b-9d86-2e7d51ca6509 | Продукты | Тип: EXPENSE | Еда и напитки
=====================

1. Создать категорию
2. Редактировать категорию
3. Удалить категорию
0. Назад
   Выберите пункт: 0

ГЛАВНОЕ МЕНЮ
1. Управление счетами
2. Управление категориями
3. Управление операциями
4. Аналитика
5. �?мпорт/Экспорт
6. Проверка и пересчет балансов
0. Выход
   Выберите пункт: 3

УПРАВЛЕН�?Е ОПЕРАЦ�?ЯМ�?
1. Создать операцию
2. Просмотреть операции счета
3. Просмотреть все операции
0. Назад
   Выберите пункт: 2
   ID счета: 52297fa7-ed51-4978-8f57-b8c8dd24e592

ОПЕРАЦ�?�?:
2026-02-16 | EXPENSE | 5000.00 | Рестораны
2026-02-16 | INCOME | 100000.00 | Зарплата
2026-02-16 | EXPENSE | 15000.00 | Транспорт

УПРАВЛЕН�?Е ОПЕРАЦ�?ЯМ�?
1. Создать операцию
2. Просмотреть операции счета
3. Просмотреть все операции
0. Назад
   Выберите пункт: 1

СП�?СОК СЧЕТОВ:
==================
ID: 8e9d518d-43ee-4695-8839-e1762ca40139 | Демо | Баланс: 0 RUB | Операций: 0
ID: 52297fa7-ed51-4978-8f57-b8c8dd24e592 | Основной счет | Баланс: 80000.00 RUB | Операций: 3
ID: bbb526d0-4d15-4489-aebe-f2deaa1aec18 | Накопительный | Баланс: 5000.00 RUB | Операций: 1
==================

ID счета: bb526d0-4d15-4489-aebe-f2deaa1aec18
Тип (INCOME/EXPENSE): INCOME
Сумма: 1200
Описание: Продажа кофеварки
ID категории (Enter для пропуска): b3816193-fb61-4ea8-8a38-f0fad1471c29
Ошибка: Счет с ID bb526d0-4d15-4489-aebe-f2deaa1aec18 не найден

УПРАВЛЕН�?Е ОПЕРАЦ�?ЯМ�?
1. Создать операцию
2. Просмотреть операции счета
3. Просмотреть все операции
0. Назад
   Выберите пункт: 0

ГЛАВНОЕ МЕНЮ
1. Управление счетами
2. Управление категориями
3. Управление операциями
4. Аналитика
5. �?мпорт/Экспорт
6. Проверка и пересчет балансов
0. Выход
   Выберите пункт: 4
   ID счета: 52297fa7-ed51-4978-8f57-b8c8dd24e592
   Начало периода (ГГГГ-ММ-ДД): 2000-10-10
   Конец периода (ГГГГ-ММ-ДД): 2026-01-01

АНАЛ�?Т�?КА ПО СЧЕТУ: Основной счет
Период: 2000-10-10 - 2026-01-01
===========================================
Доходы: 0 RUB
Расходы: 0 RUB
�?того: 0 RUB

Доходы по категориям:
Расходы по категориям:

ТОП-5 КРУПНЫХ ОПЕРАЦ�?Й:
===========================================


ГЛАВНОЕ МЕНЮ
1. Управление счетами
2. Управление категориями
3. Управление операциями
4. Аналитика
5. �?мпорт/Экспорт
6. Проверка и пересчет балансов
0. Выход
   Выберите пункт: 6

ПРОВЕРКА БАЛАНСОВ ВСЕХ СЧЕТОВ:
===================================
Счет 'Демо': баланс корректен (0)
Счет 'Основной счет': баланс корректен (80000.00)
Счет 'Накопительный': баланс корректен (5000.00)
===================================

Хотите пересчитать все балансы? (да/нет): нет

ГЛАВНОЕ МЕНЮ
1. Управление счетами
2. Управление категориями
3. Управление операциями
4. Аналитика
5. �?мпорт/Экспорт
6. Проверка и пересчет балансов
0. Выход
   Выберите пункт: 0
   До свидания!