package liaison.linkit.profile.domain.repository.log;

import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.profile.domain.ProfileLogImage;

public interface ProfileLogImageCustomRepository {
    List<ProfileLogImage> getUnusedProfileLogImages(final LocalDateTime localDateTime);
}
