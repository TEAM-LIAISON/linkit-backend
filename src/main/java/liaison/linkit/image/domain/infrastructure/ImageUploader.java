package liaison.linkit.image.domain.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUploader {
    private static final String CACHE_CONTROL_VALUE = "max-age=3153600";

}
