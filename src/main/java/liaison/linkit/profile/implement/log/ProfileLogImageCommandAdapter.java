package liaison.linkit.profile.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.log.ProfileLogImage;
import liaison.linkit.profile.domain.repository.log.ProfileLogImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogImageCommandAdapter {

    private final ProfileLogImageRepository profileLogImageRepository;

    public void addProfileLogImage(final ProfileLogImage profileLogImage) {
        profileLogImageRepository.save(profileLogImage);
    }

    public void removeProfileLogImage(final ProfileLogImage profileLogImage) {
        profileLogImageRepository.delete(profileLogImage);
    }
}
