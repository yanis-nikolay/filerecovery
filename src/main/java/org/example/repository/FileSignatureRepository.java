package org.example.repository;

import org.example.model.FileSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями FileSignature в базе данных.
 *
 * Расширяет стандартный JpaRepository, предоставляя методы для поиска
 * и управления подписями файлов по различным критериям.
 */
@Repository
public interface FileSignatureRepository extends JpaRepository<FileSignature, Long> {
    /**
     * Находит подпись файла по расширению.
     *
     * @param extension расширение файла для поиска
     * @return Optional с найденной подписью файла или пустой Optional
     */
    Optional<FileSignature> findByExtension(String extension);

    /**
     * Находит подпись файла по hex-сигнатуре.
     *
     * @param hexSignature hex-представление магических чисел файла
     * @return Optional с найденной подписью файла или пустой Optional
     */
    Optional<FileSignature> findByHexSignature(String hexSignature);
}