package org.example.service;

import org.example.model.FileSignature;
import org.example.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Сервис для анализа файлов и определения их подписей и типов.
 *
 * Этот сервис предоставляет functionality для идентификации типов файлов
 * на основе их магических чисел (hex-сигнатуры) и содержимого.
 * Поддерживает операции по анализу файлов и восстановлению их расширений.
 */
@Service
public class FileAnalyzeService {
    /**
     * Логгер для записи информационных и отладочных сообщений при анализе файлов.
     */
    private static final Logger logger = LoggerFactory.getLogger(FileAnalyzeService.class);

    /**
     * Сервис для работы с подписями файлов.
     */
    @Autowired
    private FileSignatureService signatureService;

    /**
     * Анализирует файл для определения его типа и подписи.
     *
     * Метод проверяет hex-сигнатуру файла, пытаясь найти соответствующую
     * подпись в базе данных. Если точное совпадение не найдено, пытается
     * определить тип как текстовый файл.
     *
     * @param file файл для анализа
     * @return {@link FileSignature} с информацией о типе файла или {@code null},
     *         если тип файла не удалось определить
     */
    public FileSignature analyzeFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            logger.error("Invalid file for analysis: {}", file);
            return null;
        }

        try {
            String hexSignature = FileUtils.getFileHexSignature(file, 32);
            if (hexSignature == null || hexSignature.isEmpty()) {
                logger.warn("Failed to get the hex signature of the file: {}", file.getName());
                return null;
            }

            Optional<FileSignature> matchedSignature = signatureService.getAllSignatures().stream()
                    .filter(signature -> !signature.getHexSignature().isEmpty() &&
                            isSignatureMatch(hexSignature, signature.getHexSignature()))
                    .findFirst();

            if (matchedSignature.isPresent()) {
                logger.info("The signature for the file was found: {}, type: {}", file.getName(), matchedSignature.get().getFileType());
                return matchedSignature.get();
            }

            if (isPlainText(file)) {
                Optional<FileSignature> textSignature = signatureService.findByExtension("txt");
                if (textSignature.isPresent()) {
                    logger.info("The file is defined as a text file: {}", file.getName());
                    return textSignature.get();
                }
            }

            logger.warn("The file type could not be determined: {}", file.getName());
            return null;

        } catch (Exception e) {
            logger.error("Error analyzing the file: {}", file.getName(), e);
            return null;
        }
    }

    /**
     * Проверяет соответствие hex-сигнатуры файла эталонной подписи.
     *
     * Метод выполняет нечеткое сравнение hex-сигнатуры с шаблоном,
     * позволяя частичные совпадения и сравнение без учета регистра.
     *
     * @param fileHexSignature hex-сигнатура анализируемого файла
     * @param signaturePattern эталонная hex-сигнатура для сравнения
     * @return {@code true}, если сигнатуры совпадают, иначе {@code false}
     */
    private boolean isSignatureMatch(String fileHexSignature, String signaturePattern) {
        if (fileHexSignature == null || signaturePattern == null) {
            return false;
        }
        return fileHexSignature.startsWith(signaturePattern) ||
                fileHexSignature.contains(signaturePattern) ||
                (signaturePattern.length() > 2 && fileHexSignature.toLowerCase().contains(signaturePattern.toLowerCase()));
    }

    /**
     * Определяет, является ли файл текстовым.
     *
     * Метод пытается прочитать содержимое файла и проверить,
     * состоит ли оно только из печатных символов.
     *
     * @param file файл для проверки
     * @return {@code true}, если файл является текстовым, иначе {@code false}
     */
    private boolean isPlainText(File file) {
        try {
            byte[] content = Files.readAllBytes(file.toPath());
            return isTextContent(content);
        } catch (IOException e) {
            logger.error("Error reading the file when checking the text content: {}", file.getName(), e);
            return false;
        }
    }

    /**
     * Проверяет, содержит ли массив байтов только текстовые символы.
     *
     * Метод конвертирует байты в строку и проверяет соответствие
     * регулярному выражению для печатных символов.
     *
     * @param content массив байтов для проверки
     * @return {@code true}, если содержимое является текстовым, иначе {@code false}
     */
    private boolean isTextContent(byte[] content) {
        try {
            String text = new String(content, StandardCharsets.UTF_8);
            return text.matches("^[\\p{Print}\\s]+$");
        } catch (Exception e) {
            logger.warn("The content could not be converted to text");
            return false;
        }
    }

    /**
     * Восстанавливает расширение файла.
     *
     * Метод переименовывает файл, используя предоставленное новое расширение.
     * Проверяет корректность входных параметров перед выполнением операции.
     *
     * @param file файл для переименования
     * @param newExtension новое расширение файла
     * @return {@code true}, если расширение успешно восстановлено, иначе {@code false}
     */
    public boolean recoverFileExtension(File file, String newExtension) {
        if (file == null || !file.exists() || newExtension == null || newExtension.isEmpty()) {
            logger.error("Invalid parameters for restoring the extension");
            return false;
        }

        try {
            String renamedFilePath = FileUtils.renameFile(file, newExtension);
            if (renamedFilePath != null) {
                logger.info("The file extension has been successfully restored: {} -> {}", file.getName(), renamedFilePath);
                return true;
            } else {
                logger.warn("The file could not be renamed: {}", file.getName());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error restoring file extension", e);
            return false;
        }
    }
}