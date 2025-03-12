package liaison.linkit.team.presentation.announcement.dto;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementProgressDTO {

    // 처리할 팀 멤버 공고
    private TeamMemberAnnouncement teamMemberAnnouncement;

    // 공고의 진행 상태 (true: 진행 중, false: 마감)
    private boolean isTeamMemberAnnouncementInProgress;
}
