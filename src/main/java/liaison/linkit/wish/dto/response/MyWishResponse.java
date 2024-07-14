package liaison.linkit.wish.dto.response;

import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyWishResponse {
    final List<MiniProfileResponse> miniProfileResponseList;
    final List<TeamMiniProfileResponse> teamMiniProfileResponseList;
}
