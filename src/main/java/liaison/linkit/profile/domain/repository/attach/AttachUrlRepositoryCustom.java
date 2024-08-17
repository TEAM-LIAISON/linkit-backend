package liaison.linkit.profile.domain.repository.attach;

import liaison.linkit.profile.domain.attach.AttachUrl;

import java.util.List;

public interface AttachUrlRepositoryCustom {
    List<AttachUrl> findAllByProfileId(final Long profileId);
}
