package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;

@Mapper
public class ProfileCurrentStateMapper {

    public List<ProfileCurrentStateItem> toProfileCurrentStateItems(
            final List<ProfileCurrentState> profileCurrentStates) {
        return profileCurrentStates.stream()
                .map(currentState -> ProfileCurrentStateItem.builder()
                        .profileStateName(currentState.getProfileState().getProfileStateName())
                        .build())
                .collect(Collectors.toList());
    }

}
