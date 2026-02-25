# Паттерны: Строитель, фабрика

Добавлены:
- интерфейс фабрики спутников SatelliteFactory с реализациями CommunicationSatelliteFactory и ImagingSatelliteFactory;
- пошаговая сборка EnergySystem через @builder библиотеки lombok

```PowerShell
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
{Орбита-2=[seminars.domain.ImagingSatellite{bandWidth=1000.0, name=Связь-2, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0,75}}, seminars.domain.ImagingSatellite{resolution=0.5, photosTaken0, name=ДЗЗ-3, state=SatelliteState{isActive=false, statusMessage=Не активирован.}', energy=EnergySystem{ batteryLevel=0,15}}], Орбита-1=[seminars.domain.ImagingSatellite{bandWidth=500.0, name=Связь-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,80}}, seminars.domain.ImagingSatellite{resolution=2.5, photosTaken1, name=ДЗЗ-1, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,84}}, seminars.domain.ImagingSatellite{resolution=1.0, photosTaken1, name=ДЗЗ-2, state=SatelliteState{isActive=true, statusMessage=Активен}', energy=EnergySystem{ batteryLevel=0,37}}]}
```