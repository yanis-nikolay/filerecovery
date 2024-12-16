package org.example.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Конфигурация базы данных для Spring Boot приложения.
 *
 * Этот класс настраивает сканирование JPA репозиториев и сущностей.
 * Используется для автоматической настройки Spring Data JPA и определения
 * базовых пакетов для репозиториев и сущностей.
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.example.repository")
@EntityScan(basePackages = "org.example.model")
public class DatabaseConfig {
}