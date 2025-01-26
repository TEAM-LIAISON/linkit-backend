package liaison.linkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LinkitApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkitApplication.class, args);
    }
}
