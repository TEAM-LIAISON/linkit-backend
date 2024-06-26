package liaison.linkit.team.dto.response.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamMemberAnnouncementResponse {

    private final String mainBusiness;

    public static TeamMemberAnnouncementResponse of(
            final TeamMemberAnnouncement teamMemberAnnouncement
    ) {
        return new TeamMemberAnnouncementResponse(
                teamMemberAnnouncement.getMainBusiness()
        );
    }
}
