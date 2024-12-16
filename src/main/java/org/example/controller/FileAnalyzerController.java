package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.model.FileSignature;
import org.example.service.FileAnalyzeService;
import org.example.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Контроллер JavaFX для графического интерфейса анализатора файловых подписей.
 *
 * Управляет пользовательским интерфейсом для:
 * - Выбора файла
 * - Анализа файловой подписи
 * - Отображения информации о файле
 * - Восстановления расширения файла
 *
 * Связывает логику {@link FileAnalyzeService} с визуальными компонентами.
 */
@Component
public class FileAnalyzerController {
    /** Текстовое поле для отображения пути к выбранному файлу. */
    @FXML private TextField filePathField;
    /** Метка для отображения текущего расширения файла. */
    @FXML private Label currentExtensionLabel;
    /** Метка для отображения MIME-типа файла. */
    @FXML private Label mimeTypeLabel;
    /** Метка для отображения магических чисел файла. */
    @FXML private Label magicNumbersLabel;
    /** Текстовая область для вывода результатов анализа файла. */
    @FXML private TextArea resultTextArea;
    /** Кнопка для запуска анализа файла. */
    @FXML private Button analyzeButton;
    /** Кнопка для восстановления расширения файла. */
    @FXML private Button recoverButton;
    /** Кнопка для очистки полей интерфейса. */
    @FXML private Button clearButton;

    /** Выбранный файл для анализа. */
    private File selectedFile;
    /** Текущая подпись файла, определенная в результате анализа. */
    private FileSignature currentSignature;

    /** Сервис для анализа файлов. */
    @Autowired
    private FileAnalyzeService analyzeService;

    /**
     * Инициализация контроллера после загрузки FXML.
     *
     * Вызывается автоматически после загрузки view.
     * Сбрасывает интерфейс и устанавливает слушатель для поля пути файла.
     */
    @FXML
    public void initialize() {
        resetUI();
        setupFilePathFieldListener();
    }

    /**
     * Сбрасывает элементы пользовательского интерфейса в начальное состояние.
     *
     * Очищает все поля, метки и возвращает кнопки в исходное положение.
     */
    private void resetUI() {
        filePathField.clear();
        currentExtensionLabel.setText("Не определено");
        mimeTypeLabel.setText("Не определено");
        magicNumbersLabel.setText("Не определено");
        resultTextArea.setText("Выберите файл для анализа");
        analyzeButton.setDisable(true);
        recoverButton.setDisable(true);
        currentSignature = null;
        selectedFile = null;
    }

    /**
     * Настраивает прослушиватель для поля ввода пути файла.
     *
     * При изменении текста в поле проверяет существование файла
     * и автоматически запускает его анализ.
     */
    private void setupFilePathFieldListener() {
        filePathField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                selectedFile = new File(newValue);
                if (selectedFile.exists()) {
                    analyzeFileAutomatically();
                } else {
                    resetUI();
                }
            }
        });
    }

    /**
     * Автоматически анализирует выбранный файл.
     *
     * Получает hex-сигнатуру, определяет расширение,
     * анализирует файл с помощью сервиса и обновляет интерфейс.
     */
    private void analyzeFileAutomatically() {
        String hexSignature = FileUtils.getFileHexSignature(selectedFile, 16);
        magicNumbersLabel.setText(hexSignature != null ? hexSignature : "Не удалось определить");
        currentExtensionLabel.setText(FileUtils.getFileExtension(selectedFile));

        currentSignature = analyzeService.analyzeFile(selectedFile);

        if (currentSignature != null) {
            mimeTypeLabel.setText(currentSignature.getMimeType());

            boolean needExtensionRecovery = currentExtensionLabel.getText().isEmpty() ||
                    (!currentSignature.getExtension().toLowerCase().contains(currentExtensionLabel.getText().toLowerCase()));

            resultTextArea.setText(
                    "Файл " + (needExtensionRecovery ? "требует восстановления расширения" : "исправен") + "\n\n" +
                            "Тип файла: " + currentSignature.getFileType() + "\n" +
                            "Описание: " + currentSignature.getDescription() + "\n" +
                            "Магические числа: " + hexSignature
            );

            analyzeButton.setDisable(true);
            recoverButton.setDisable(!needExtensionRecovery);
        } else {
            resultTextArea.setText("Не удалось определить тип файла");
            analyzeButton.setDisable(false);
            recoverButton.setDisable(true);
        }
    }

    /**
     * Обработчик события выбора файла через диалоговое окно.
     *
     * Открывает стандартный диалог выбора файла и устанавливает
     * выбранный файл в поле пути.
     */
    @FXML
    public void handleFileSelection() {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Отображает справочный диалог с инструкциями по использованию приложения.
     *
     * Показывает информационное окно с основными шагами работы
     * в File Signature Analyzer.
     */
    @FXML
    public void handleHelp() {
        Alert helpDialog = new Alert(Alert.AlertType.INFORMATION);
        helpDialog.setTitle("Помощь");
        helpDialog.setHeaderText("Как пользоваться File Signature Analyzer");
        helpDialog.setContentText(
                "1. Выберите файл с помощью кнопки 'Обзор'\n" +
                        "2. 'Определить тип' покажет тип и описание файла\n" +
                        "3. 'Восстановить расширение' изменит расширение файла\n\n" +
                        "Магические числа - уникальный идентификатор типа файла"
        );
        helpDialog.showAndWait();
    }

    /**
     * Очищает все поля пользовательского интерфейса.
     *
     * Вызывает метод resetUI() для возврата интерфейса
     * в начальное состояние.
     */
    @FXML
    public void handleClearFields() {
        resetUI();
    }

    /**
     * Обработчик ручного запуска анализа файла.
     *
     * Проверяет наличие выбранного файла и запускает его анализ,
     * обновляя интерфейс результатами.
     */
    @FXML
    public void handleAnalyzeFile() {
        if (selectedFile == null) {
            showAlert("Ошибка", "Сначала выберите файл!");
            return;
        }

        String hexSignature = FileUtils.getFileHexSignature(selectedFile, 16);
        magicNumbersLabel.setText(hexSignature != null ? hexSignature : "Не удалось определить");

        currentSignature = analyzeService.analyzeFile(selectedFile);

        if (currentSignature != null) {
            mimeTypeLabel.setText(currentSignature.getMimeType());

            boolean needExtensionRecovery = currentExtensionLabel.getText().isEmpty() ||
                    (!currentSignature.getExtension().toLowerCase().contains(currentExtensionLabel.getText().toLowerCase()));

            resultTextArea.setText(
                    "Тип файла: " + currentSignature.getFileType() + "\n" +
                            "Описание: " + currentSignature.getDescription() + "\n" +
                            (needExtensionRecovery ? "Требуется восстановление расширения" : "Файл исправен")
            );

            recoverButton.setDisable(!needExtensionRecovery);
        } else {
            resultTextArea.setText("Не удалось определить тип файла");
            recoverButton.setDisable(true);
        }
    }

    /**
     * Обработчик восстановления расширения файла.
     *
     * Пытается восстановить расширение файла с помощью сервиса
     * и обновляет интерфейс результатом операции.
     */
    @FXML
    public void handleRecoverExtension() {
        if (selectedFile == null || currentSignature == null) {
            showAlert("Ошибка", "Сначала выберите файл!");
            return;
        }

        String newExtension = currentSignature.getExtension();
        boolean recovered = analyzeService.recoverFileExtension(selectedFile, newExtension);

        if (recovered) {
            resultTextArea.setText("Расширение восстановлено: " + newExtension);
            currentExtensionLabel.setText(newExtension);
            filePathField.setText(selectedFile.getAbsolutePath());
            recoverButton.setDisable(true);
        } else {
            showAlert("Ошибка", "Не удалось восстановить расширение");
        }
    }

    /**
     * Отображает диалоговое окно с сообщением об ошибке.
     *
     * @param title заголовок окна ошибки
     * @param message текст сообщения об ошибке
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}