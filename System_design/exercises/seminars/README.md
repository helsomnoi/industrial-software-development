В третьем семинаре был реализован DI-контейнер для новых разработанных классов ConstellationRepository и SpaceOperationCenterService. 
Для этого был использован плагин SpringBoot.

ConstellationRepository -хранение спутниковых группировок, в дальнейшем будет реализован через БД;

SpaceOperationCenterService - сервис для работы с группировками, реализующий принцип единственной ответственности.

Вывод системы:

Task :seminars.Main.main()
ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ
=========================

.   ____          _            __ _ _
/\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
'  |____| .__|_| |_|_| |_\__, | / / / /
=========|_|==============|___/=/_/_/_/
:: Spring Boot ::                (v3.2.2)

2026-02-13T18:21:21.854+03:00  INFO 3916 --- [           main] seminars.Main                            : Starting Main using Java 17.0.12 with PID 3916 (C:\Users\R2\IdeaProjects\study\build\classes\java\main started by R2 in C:\Users\R2\IdeaProjects\study)
2026-02-13T18:21:21.857+03:00  INFO 3916 --- [           main] seminars.Main                            : No active profile set, falling back to 1 default profile: "default"
2026-02-13T18:21:22.394+03:00  INFO 3916 --- [           main] seminars.Main                            : Started Main in 0.866 seconds (process running for 1.171)
СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:
---------------
Создан спутник: Связь-1 (заряд: 85.0)
Создан спутник: Связь-2 (заряд: 75.0)
Создан спутник: ДЗЗ-1 (заряд: 92.0)
Создан спутник: ДЗЗ-2 (заряд: 45.0)
Создан спутник: ДЗЗ-3 (заряд: 15.0)
---------------
Создана спутниковая группировка: Орбита-1
Сохранена группировака: Орбита-1
Создана спутниковая группировка: Орбита-2
Сохранена группировака: Орбита-2
---------------
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
Количество спутников: Орбита-1
SatelliteState{isActive=false, statusMessage=Не активирован.}'
SatelliteState{isActive=false, statusMessage=Не активирован.}'
SatelliteState{isActive=false, statusMessage=Не активирован.}'

=== СТАТУС ГРУППИРОВКИ: Орбита-2 ===
Количество спутников: Орбита-2
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
Количество спутников: Орбита-1
SatelliteState{isActive=true, statusMessage=Активен}'
SatelliteState{isActive=true, statusMessage=Активен}'
SatelliteState{isActive=true, statusMessage=Активен}'
{Орбита-2=[seminars.ImagingSatellite{bandWidth=1000.0, name=Связь-2, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0.75 }}, seminars.ImagingSatellite{resolution=0.5, photosTaken0, name=ДЗЗ-3, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0.15 }}], Орбита-1=[seminars.ImagingSatellite{bandWidth=500.0, name=Связь-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0.7999999999999999 }}, seminars.ImagingSatellite{resolution=2.5, photosTaken1, name=ДЗЗ-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0.8400000000000001 }}, seminars.ImagingSatellite{resolution=1.0, photosTaken1, name=ДЗЗ-2, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0.37 }}]}
