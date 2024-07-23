package liaison.linkit.wish.dto.response;

import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WishTeamProfileResponse {
    private final TeamMiniProfileResponse teamMiniProfileResponse;
    private final TeamMemberAnnouncementResponse teamMemberAnnouncementResponse;
}
