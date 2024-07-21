package liaison.linkit.wish.dto.response;

import liaison.linkit.search.dto.response.browseAfterLogin.BrowseTeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WishTeamProfileResponse {
    private final TeamMiniProfileResponse teamMiniProfileResponse;
    private final BrowseTeamMemberAnnouncementResponse browseTeamMemberAnnouncementResponse;
}
