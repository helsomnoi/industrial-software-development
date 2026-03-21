package com.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Загрузка из application.properties
public class PropertiesLoader {
    public static Properties load(String resourceName) throws IOException {
        try (InputStream input = PropertiesLoader.class.getClassLoader()
                .getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IOException("Файл " + resourceName + " не найден в classpath");
            }
            Properties props = new Properties();
            props.load(input);
            return props;
        }
    }
}