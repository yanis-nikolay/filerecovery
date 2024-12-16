package org.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Утилитарный класс для работы с файлами.
 * Предоставляет статические методы для получения сигнатуры, расширения и переименования файлов.
 */
public class FileUtils {
    /**
     * Получает hex-сигнатуру файла.
     *
     * @param file файл, для которого требуется получить сигнатуру
     * @param bytesToRead количество байт для чтения в сигнатуру
     * @return строка hex-сигнатуры или null, если не удалось прочитать файл
     */
    public static String getFileHexSignature(File file, int bytesToRead) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[bytesToRead];
            int bytesRead = fis.read(buffer);

            if (bytesRead == -1) {
                return null;
            }

            StringBuilder hexSignature = new StringBuilder();
            for (int i = 0; i < bytesRead; i++) {
                hexSignature.append(String.format("%02X", buffer[i]));
            }

            return hexSignature.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Получает расширение файла.
     *
     * @param file файл, для которого требуется получить расширение
     * @return расширение файла в нижнем регистре или пустая строка, если расширения нет
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf + 1).toLowerCase();
    }

    /**
     * Переименовывает файл с новым расширением.
     *
     * @param file файл для переименования
     * @param newExtension новое расширение
     * @return путь к переименованному файлу или null, если переименование не удалось
     */
    public static String renameFile(File file, String newExtension) {
        String fileName = file.getName();

        // Находим последнее вхождение точки
        int lastIndexOf = fileName.lastIndexOf(".");

        // Если точки нет, просто добавляем новое расширение
        if (lastIndexOf == -1) {
            return renameFileWithFullName(file, newExtension);
        }

        // Проверяем, не является ли "расширение" слишком длинным или странным
        String currentExtension = fileName.substring(lastIndexOf + 1);
        if (currentExtension.length() > 10) {
            return renameFileWithFullName(file, newExtension);
        }

        // Стандартный случай - просто заменяем расширение
        newExtension = newExtension.toLowerCase();
        String newFileName = fileName.substring(0, lastIndexOf) + "." + newExtension;

        File newFile = new File(file.getParent(), newFileName);

        if (file.renameTo(newFile)) {
            return newFile.getAbsolutePath();
        }

        return null;
    }

    /**
     * Переименовывает файл, добавляя новое расширение к полному имени файла.
     *
     * @param file файл для переименования
     * @param newExtension новое расширение
     * @return путь к переименованному файлу или null, если переименование не удалось
     */
    private static String renameFileWithFullName(File file, String newExtension) {
        String newFileName = file.getName() + "." + newExtension.toLowerCase();
        File newFile = new File(file.getParent(), newFileName);

        if (file.renameTo(newFile)) {
            return newFile.getAbsolutePath();
        }

        return null;
    }
}