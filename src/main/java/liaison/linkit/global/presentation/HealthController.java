package liaison.linkit.global.presentation;

import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HealthController {

    @Value("${server.env}")
    private String env;

    @GetMapping("/hc")
    public ResponseEntity<?> healthcheck() {
        Map<String, String> responseData = new TreeMap<>();
        responseData.put("env", env);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {

        return ResponseEntity.ok(env);
    }
}
