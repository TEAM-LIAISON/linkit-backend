package liaison.linkit.image.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.image.domain.Image;
import liaison.linkit.image.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ImageCommandAdapter {
    private final ImageRepository imageRepository;

    public Image addImage(final Image image) {
        return imageRepository.save(image);
    }

    public void removeImage(final Image image) {
        imageRepository.delete(image);
    }
}
