package liaison.linkit.profile.implement.log;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.ProfileLogImage;
import liaison.linkit.profile.domain.repository.log.ProfileLogImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogImageQueryAdapter {

    private final ProfileLogImageRepository profileLogImageRepository;

    public List<ProfileLogImage> findByProfileLog(final ProfileLog profileLog) {
        return profileLogImageRepository.findByProfileLog(profileLog);
    }
}
