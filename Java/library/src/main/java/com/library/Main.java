package com.library;

import com.library.console.ConsoleRun;
import com.library.util.DatabaseManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager.initializeSchema();
        } catch (Exception e) {
            System.err.println("Не удалось инициализировать схему БД. Приложение завершает работу.");
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Загрузить тестовые данные? (да/нет): ");
        String answer = scanner.nextLine().trim();
        if (answer.equalsIgnoreCase("да") || answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
            try {
                DatabaseManager.loadTestData();
            } catch (Exception e) {
                System.err.println("Ошибка загрузки тестовых данных: " + e.getMessage());
            }
        }

        new ConsoleRun(scanner).start();
    }
}