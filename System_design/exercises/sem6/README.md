# Паттерны: Стратегия, оптимизация Фабричного метода

В 6 семинаре:
- добавлены абстрактные классы SatelliteParam с наследниками CommunicationSatelliteParam и ImagingSatelliteParam;
- обновлены классы фабрик SatelliteFactory, CommunicationSatelliteFactory, ImagingSatelliteFactory;
- добавлен сервис SatelliteService для создания спутников через фабрики.
- обновлен тест SatelliteFactoryTest;
- разработан SatelliteServiceTest

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

2026-03-04T20:07:09.871+03:00  INFO 15000 --- [           main] seminars.SatelliteApplication            : Starting SatelliteApplication using Java 17.0.12 with PID 15000 (C:\Lena\учеба\uni\Индустриальная разработка ПО (Бюро)\System Design\exercises\study\build\classes\java\main started by R2 in C:\Lena\учеба\uni\Индустриальная разработка ПО (Бюро)\System Design\exercises\study)
2026-03-04T20:07:09.871+03:00  INFO 15000 --- [           main] seminars.SatelliteApplication            : No active profile set, falling back to 1 default profile: "default"
2026-03-04T20:07:10.301+03:00  INFO 15000 --- [           main] seminars.SatelliteApplication            : Started SatelliteApplication in 0.65 seconds (process running for 0.943)
ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ
=========================
СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:
---------------
Создан спутник: Связь-1 (заряд: 85.0)
Создан спутник: Связь-2 (заряд: 75.0)
Создан спутник: ДЗЗ-1 (заряд: 92.0)
Создан спутник: ДЗЗ-2 (заряд: 45.0)
Создан спутник: ДЗЗ-3 (заряд: 15.0)
---------------
Создана спутниковая группировка: Орбита-1
Сохранена группировка: Орбита-1
Создана спутниковая группировка: Орбита-2
Сохранена группировка: Орбита-2
ДОБАВЛЕНИЕ СПУТНИКОВ:
Связь-1 добавлен в группировку 'Орбита-1'
Добавлен спутник Связь-1 в группировку Орбита-1
ДЗЗ-1 добавлен в группировку 'Орбита-1'
Добавлен спутник ДЗЗ-1 в группировку Орбита-1
ДЗЗ-2 добавлен в группировку 'Орбита-1'
Добавлен спутник ДЗЗ-2 в группировку Орбита-1
Связь-2 добавлен в группировку 'Орбита-2'
Добавлен спутник Связь-2 в группировку Орбита-2
ДЗЗ-3 добавлен в группировку 'Орбита-2'
Добавлен спутник ДЗЗ-3 в группировку Орбита-2
----------

=== СТАТУС ГРУППИРОВКИ: Орбита-1 ===
Количество спутников: 3
SatelliteState{isActive=false, statusMessage=Не активирован.}'
SatelliteState{isActive=false, statusMessage=Не активирован.}'
SatelliteState{isActive=false, statusMessage=Не активирован.}'

=== СТАТУС ГРУППИРОВКИ: Орбита-2 ===
Количество спутников: 2
SatelliteState{isActive=false, statusMessage=Не активирован.}'
SatelliteState{isActive=false, statusMessage=Не активирован.}'

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

=== СТАТУС ГРУППИРОВКИ: Орбита-1 ===
Количество спутников: 3
SatelliteState{isActive=true, statusMessage=Активен}'
SatelliteState{isActive=true, statusMessage=Активен}'
SatelliteState{isActive=true, statusMessage=Активен}'
{Орбита-2=[CommunicationSatellite{bandWidth=1000.0, name=Связь-2, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0,75}}, ImagingSatellite{resolution=0.5, photosTaken0, name=ДЗЗ-3, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0,15}}], Орбита-1=[CommunicationSatellite{bandWidth=500.0, name=Связь-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,80}}, ImagingSatellite{resolution=2.5, photosTaken1, name=ДЗЗ-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,84}}, ImagingSatellite{resolution=1.0, photosTaken1, name=ДЗЗ-2, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,37}}]}

[Incubating] Problems report is available at: file:///C:/Lena/%D1%83%D1%87%D0%B5%D0%B1%D0%B0/uni/%D0%98%D0%BD%D0%B4%D1%83%D1%81%D1%82%D1%80%D0%B8%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%80%D0%B0%D0%B7%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0%20%D0%9F%D0%9E%20(%D0%91%D1%8E%D1%80%D0%BE)/System%20Design/exercises/study/build/reports/problems/problems-report.html

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/8.14/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.

BUILD SUCCESSFUL in 3s
3 actionable tasks: 1 executed, 2 up-to-date
```