package liaison.linkit.image.implement;

import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.image.domain.Image;
import liaison.linkit.image.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ImageQueryAdapter {

    private final ImageRepository imageRepository;

    public List<Image> getUnusedImages(final LocalDateTime threshold) {
        return imageRepository.getUnusedImages(threshold);
    }

    public List<Image> findByImageUrls(final List<String> imagePaths) {
        return imageRepository.findByImageUrls(imagePaths);
    }
}
