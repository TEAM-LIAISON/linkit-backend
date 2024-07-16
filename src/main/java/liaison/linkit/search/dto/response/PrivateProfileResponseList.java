package liaison.linkit.search.dto.response;

import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrivateProfileResponseList {
    private final MiniProfileResponse miniProfileResponse;
}

