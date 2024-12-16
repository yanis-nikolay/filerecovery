package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет сущность подписи файла в базе данных.
 *
 * Класс описывает характеристики файла, такие как расширение, MIME-тип,
 * магические числа (hex-сигнатура) и тип файла.
 * Используется для идентификации и классификации файлов.
 */
@Data
@Entity
@Table(name = "file_signatures")
@NoArgsConstructor
@AllArgsConstructor
public class FileSignature {
    /**
     * Уникальный идентификатор подписи файла в базе данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Расширение файла (например, "txt", "jpg", "pdf").
     */
    private String extension;

    /**
     * MIME-тип файла (например, "text/plain", "image/jpeg").
     */
    private String mimeType;

    /**
     * Hex-представление магических чисел файла.
     */
    private String hexSignature;

    /**
     * Описание типа файла.
     */
    private String description;

    /**
     * Перечисление, определяющее тип файла (изображение, видео, аудио и т.д.).
     */
    @Enumerated(EnumType.STRING)
    private FileType fileType;
}