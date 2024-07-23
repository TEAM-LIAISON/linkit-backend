package liaison.linkit.wish.dto.response;

import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WishTeamProfileResponse {
    // 팀 찜하기 응답
    private final TeamMiniProfileResponse teamMiniProfileResponse;
    private final TeamMemberAnnouncementResponse teamMemberAnnouncementResponse;
}
