package liaison.linkit.member.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeTestController {

    @GetMapping("/health")
    public String healthCheck() {
        return "I'm healthy!!";
    }
}
