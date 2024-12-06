package liaison.linkit.profile.domain.repository.log;

import java.util.List;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.ProfileLogImage;

public interface ProfileLogImageCustomRepository {

    List<ProfileLogImage> findByProfileLog(final ProfileLog profileLog);
}
