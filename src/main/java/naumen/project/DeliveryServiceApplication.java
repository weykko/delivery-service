package naumen.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс для запуска приложения
 */
@SpringBootApplication
public class DeliveryServiceApplication {

    /**
     * Точка входа в приложение
     */
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }

}
