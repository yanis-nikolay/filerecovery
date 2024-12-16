package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.cache.annotation.EnableCaching;

import java.io.IOException;

/**
 * Главный класс приложения для File Signature Analyzer.
 * Отвечает за инициализацию и запуск JavaFX и Spring Boot приложения.
 */
@SpringBootApplication
@EnableCaching
public class Main extends Application {
    /**
     * Логгер для записи информационных и отладочных сообщений.
     */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Контекст Spring Boot приложения.
     */
    private ConfigurableApplicationContext springContext;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Инициализация Spring Boot контекста перед запуском JavaFX приложения.
     */
    @Override
    public void init() {
        springContext = SpringApplication.run(Main.class);
    }

    /**
     * Метод запуска JavaFX приложения.
     *
     * @param primaryStage основная сцена приложения
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Application started");

            var fxmlResource = getClass().getResource("/ui/MainView.fxml");
            if (fxmlResource == null) {
                throw new IOException("Cannot find MainView.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlResource);
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            var cssResource = getClass().getResource("/styles.css");
            if (cssResource == null) {
                throw new IOException("Cannot find styles.css");
            }

            Scene scene = new Scene(root, 700, 800);
            scene.getStylesheets().add(cssResource.toExternalForm());

            primaryStage.setTitle("Восстановление расширения файла");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            primaryStage.setOnCloseRequest(event -> {
                logger.info("Application is closing");

                Platform.runLater(() -> {
                    if (springContext != null) {
                        springContext.close();
                    }

                    Platform.exit();
                    System.exit(0);
                });
            });

            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error when launching the application", e);
            e.printStackTrace();

            Platform.runLater(() -> {
                if (springContext != null) {
                    springContext.close();
                }
                Platform.exit();
                System.exit(1);
            });
        }
    }

    /**
     * Дополнительный метод закрытия для полноты.
     * Вызывается при штатном закрытии приложения.
     */
    @Override
    public void stop() {
        logger.info("Application stop method called");

        if (springContext != null) {
            springContext.close();
        }

        Platform.exit();
        System.exit(0);
    }
}