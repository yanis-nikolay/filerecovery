package org.example.service;

import org.example.model.FileSignature;
import org.example.repository.FileSignatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления подписями файлов с поддержкой кэширования.
 *
 * Предоставляет методы для работы с подписями файлов, включая:
 * - Получение всех подписей
 * - Поиск подписи по расширению файла
 * - Поиск подписи по hex-сигнатуре
 *
 * Использует кэширование для оптимизации производительности при
 * повторных запросах одних и тех же данных.
 */
@Service
public class FileSignatureService {
    /**
     * Логгер для регистрации событий, связанных с операциями над подписями файлов.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileSignatureService.class);

    /**
     * Репозиторий для выполнения операций с базой данных подписей файлов.
     */
    @Autowired
    private FileSignatureRepository repository;

    /**
     * Получает все подписи файлов с кэшированием результата.
     *
     * Метод загружает все записи подписей файлов из базы данных.
     * Результат кэшируется для повышения производительности при
     * последующих вызовах.
     *
     * @return список всех подписей файлов
     * @throws RuntimeException если не удалось загрузить подписи
     */
    @Cacheable("allSignatures")
    public List<FileSignature> getAllSignatures() {
        try {
            List<FileSignature> signatures = repository.findAll();
            logger.info("All file signatures have been uploaded: {} records", signatures.size());
            return signatures;
        } catch (Exception e) {
            logger.error("Error receiving all file signatures", e);
            throw new RuntimeException("Failed to upload file signatures", e);
        }
    }

    /**
     * Находит подпись файла по расширению с кэшированием результата.
     *
     * Выполняет поиск подписи файла в базе данных по указанному расширению.
     * Результат кэшируется для оптимизации повторных запросов.
     *
     * @param extension расширение файла (не чувствительно к регистру)
     * @return {@link Optional} с найденной подписью или пустой {@link Optional}
     */
    @Cacheable("signaturesByExtension")
    public Optional<FileSignature> findByExtension(String extension) {
        try {
            Optional<FileSignature> signature = repository.findByExtension(extension.toLowerCase());
            if (signature.isPresent()) {
                logger.info("The signature for the extension was found: {}", extension);
            } else {
                logger.warn("The signature for the extension {} was not found", extension);
            }
            return signature;
        } catch (Exception e) {
            logger.error("Error when searching for an extension signature: {}", extension, e);
            return Optional.empty();
        }
    }

    /**
     * Находит подпись файла по hex-сигнатуре с кэшированием результата.
     *
     * Выполняет поиск подписи файла в базе данных по указанной hex-сигнатуре.
     * Результат кэшируется для оптимизации повторных запросов.
     *
     * @param hexSignature hex-представление магических чисел файла
     * @return {@link Optional} с найденной подписью или пустой {@link Optional}
     */
    @Cacheable("signaturesByHexSignature")
    public Optional<FileSignature> findByHexSignature(String hexSignature) {
        try {
            Optional<FileSignature> signature = repository.findByHexSignature(hexSignature);
            if (signature.isPresent()) {
                logger.info("The signature for the hex signature was found: {}", hexSignature);
            } else {
                logger.warn("The signature for the hex signature {} was not found", hexSignature);
            }
            return signature;
        } catch (Exception e) {
            logger.error("Error when searching for a signature using a hex signature: {}", hexSignature, e);
            return Optional.empty();
        }
    }
}