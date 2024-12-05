package liaison.linkit.image.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.image.domain.Image;

public interface ImageCustomRepository {

    List<Image> getUnusedImages(final LocalDateTime threshold);

    List<Image> findByImageUrls(final List<String> imagePaths);
}
