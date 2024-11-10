package liaison.linkit.profile.implement.position;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.activity.ProfileActivityRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfilePositionCommandAdapter {

    final ProfileActivityRepository profileActivityRepository;


}
