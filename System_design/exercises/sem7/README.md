# Паттерны: Стратегия, оптимизация Фабричного метода

В 7 семинаре:
- был выполнен рефакторинг с использованием паттерна Facade: 
существующий сервис ConstellationService был переименован 
и сохранён для работы непосредственно с группировками, 
а новый класс SpaceOperationCenterService стал фасадом, 
агрегирующим все бизнес-сценарии. 
Через него теперь выполняются операции добавления спутников 
(addSatellite) и запуска миссий (executeMission), 
что значительно упрощает взаимодействие с подсистемой
для клиентского кода. Дополнительно были реализованы методы 
для получения информации о состоянии группировок, 
что расширяет функциональность фасада и делает его более
удобным для пользователей.;
- внедрение паттерна Decorator с использованием 
аспектно-ориентированного программирования (AOP). 
Мы создали собственную аннотацию @LogExecutionTime и 
соответствующий аспект LoggingAspect, который перехватывает 
вызовы методов, помеченных этой аннотацией, и измеряет время 
их выполнения. Результат замеров выводится в консоль, что 
позволяет отслеживать производительность ключевых операций 
без вторжения в их бизнес-логику;
- обновлен тест SatelliteServiceTest;

```PowerShell
PS C:\Lena\учеба\uni\Индустриальная разработка ПО (Бюро)\System Design\exercises\study> .\gradlew BootRun                                       

> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.2)

2026-03-13T20:40:17.896+03:00  INFO 6408 --- [           main] seminars.SatelliteApplication            : Starting SatelliteApplication using Java 17.0.12 with PID 6408 (C:\Lena\учеба\uni\Индустриальная разработка ПО (Бюро)\System Design\exercises\study\build\classes\java\main started by R2 in C:\Lena\учеба\uni\Индустриальная разработка ПО (Бюро)\System Design\exercises\study)
2026-03-13T20:40:17.900+03:00  INFO 6408 --- [           main] seminars.SatelliteApplication            : No active profile set, falling back to 1 default profile: "default"
2026-03-13T20:40:18.497+03:00  INFO 6408 --- [           main] seminars.SatelliteApplication            : Started SatelliteApplication in 0.985 seconds (process running for 1.236)
ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ
=========================
ДОБАВЛЕНИЕ СПУТНИКОВ В ГРУППИРОВКУ 'Орбита-1':
Создана спутниковая группировка: Орбита-1
Сохранена группировка: Орбита-1
Создан спутник: Связь-1 (заряд: 85.0)
Связь-1 добавлен в группировку 'Орбита-1'
Добавлен спутник Связь-1 в группировку Орбита-1
Создан спутник: ДЗЗ-1 (заряд: 92.0)
ДЗЗ-1 добавлен в группировку 'Орбита-1'
Добавлен спутник ДЗЗ-1 в группировку Орбита-1
Создан спутник: ДЗЗ-2 (заряд: 45.0)
ДЗЗ-2 добавлен в группировку 'Орбита-1'
Добавлен спутник ДЗЗ-2 в группировку Орбита-1
Метод SpaceOperationCenterServiceImpl.addSatellite(..) выполнен за 0 мс 
ДОБАВЛЕНИЕ СПУТНИКОВ В ГРУППИРОВКУ 'Орбита-2':
Создана спутниковая группировка: Орбита-2
Сохранена группировка: Орбита-2
Создан спутник: Связь-2 (заряд: 75.0)
Связь-2 добавлен в группировку 'Орбита-2'
Добавлен спутник Связь-2 в группировку Орбита-2
Создан спутник: ДЗЗ-3 (заряд: 15.0)
ДЗЗ-3 добавлен в группировку 'Орбита-2'
Добавлен спутник ДЗЗ-3 в группировку Орбита-2
Метод SpaceOperationCenterServiceImpl.addSatellite(..) выполнен за 0 мс 

СОСТОЯНИЕ ГРУППИРОВОК ПОСЛЕ ДОБАВЛЕНИЯ:
Группировка Орбита-1:
  - Связь-1, заряд: 0.85, активен: false
  - ДЗЗ-1, заряд: 0.92, активен: false
  - ДЗЗ-2, заряд: 0.45, активен: false
Группировка Орбита-2:
  - Связь-2, заряд: 0.75, активен: false
  - ДЗЗ-3, заряд: 0.15, активен: false

ВЫПОЛНЕНИЕ МИССИИ ДЛЯ ГРУППИРОВКИ 'Орбита-1':

=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: Орбита-1 ===
Связь-1: Активация успешна!
ДЗЗ-1: Активация успешна!
ДЗЗ-2: Активация успешна!

=== ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ: Орбита-1 ===
Связь-1: Передача данных со скоростью 500.0 Мбит/с
Связь-1: Отправил 500.0 Мбит данных!
ДЗЗ-1: Съемка территории с разрешением 2.5 м/пиксель
ДЗЗ-1: Снимок №1 сделан!
ДЗЗ-2: Съемка территории с разрешением 1.0 м/пиксель
ДЗЗ-2: Снимок №1 сделан!
Метод SpaceOperationCenterServiceImpl.executeMission(..) выполнен за 0 мс 

СОСТОЯНИЕ ГРУППИРОВОК ПОСЛЕ МИССИИ:
Группировка Орбита-1:
  - Связь-1, заряд: 0.7999999999999999, активен: true
  - ДЗЗ-1, заряд: 0.8400000000000001, активен: true
  - ДЗЗ-2, заряд: 0.37, активен: true
Группировка Орбита-2:
  - Связь-2, заряд: 0.75, активен: false
  - ДЗЗ-3, заряд: 0.15, активен: false
{Орбита-2=[CommunicationSatellite{bandWidth=1000.0, name=Связь-2, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0,75}}, ImagingSatellite{resolution=0.5, photosTaken0, name=ДЗЗ-3, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0,15}}], Орбита-1=[CommunicationSatellite{bandWidth=500.0, name=Связь-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,80}}, ImagingSatellite{resolution=2.5, photosTaken1, name=ДЗЗ-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,84}}, ImagingSatellite{resolution=1.0, photosTaken1, name=ДЗЗ-2, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,37}}]}

[Incubating] Problems report is available at: file:///C:/Lena/%D1%83%D1%87%D0%B5%D0%B1%D0%B0/uni/%D0%98%D0%BD%D0%B4%D1%83%D1%81%D1%82%D1%80%D0%B8%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%80%D0%B0%D0%B7%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0%20%D0%9F%D0%9E%20(%D0%91%D1%8E%D1%80%D0%BE)/System%20Design/exercises/study/build/reports/problems/problems-report.html

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/8.14/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.

BUILD SUCCESSFUL in 2s
3 actionable tasks: 1 executed, 2 up-to-date
```