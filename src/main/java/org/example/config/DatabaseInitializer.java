package org.example.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Компонент для инициализации базы данных при запуске приложения.
 *
 * Этот класс автоматически загружает предопределенные SQL-скрипты
 * при инициализации Spring-контекста, используя аннотацию {@code @PostConstruct}.
 * Помогает заполнить базу данных начальными данными из SQL-файла.
 */
@Component
public class DatabaseInitializer {
    /**
     * Источник данных для подключения к базе данных.
     */
    @Autowired
    private DataSource dataSource;

    /**
     * Метод инициализации базы данных, вызываемый после создания бина.
     *
     * Загружает SQL-скрипт из файла {@code database/db.sql} и выполняет его
     * для заполнения базы данных начальными данными.
     *
     * @throws RuntimeException если не удалось инициализировать базу данных
     */
    @PostConstruct
    public void initializeDatabase() {
        try {
            ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("database/db.sql"));
            resourceDatabasePopulator.execute(dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}