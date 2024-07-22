package liaison.linkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LinkitApplication {
	public static void main(String[] args) {
		SpringApplication.run(LinkitApplication.class, args);
	}
}
