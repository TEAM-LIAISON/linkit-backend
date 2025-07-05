package liaison.linkit.image.domain.repository;

import liaison.linkit.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long>, ImageCustomRepository {}
