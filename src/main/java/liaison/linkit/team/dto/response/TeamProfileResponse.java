package liaison.linkit.team.dto.response;

import java.util.List;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamProfileResponse {

    private final boolean isTeamProfileEssential;

    // 4.1. 팀 미니 프로필 응답
    private final TeamMiniProfileResponse teamMiniProfileResponse;
    // 4.2.

    // 4.3. 프로필 완성도
    private final TeamCompletionResponse teamCompletionResponse;

    // 4.4. 희망 팀빌딩 분야
    private final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse;

    // 4.5. 팀원 공고
    private final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponses;

    // 4.6. 활동 방식 + 활동 지역/위치
    private final ActivityResponse activityResponse;

    // 4.7. 팀 소개
    private final TeamProfileIntroductionResponse teamProfileIntroductionResponse;

    // 4.8. 팀원 소개
    private final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponses;

    // 4.9. 연혁
    private final List<HistoryResponse> historyResponses;

    public TeamProfileResponse() {
        this.isTeamProfileEssential = false;
        this.teamMiniProfileResponse = null;
        this.teamCompletionResponse = null;
        this.teamProfileTeamBuildingFieldResponse = null;
        this.teamMemberAnnouncementResponses = null;
        this.activityResponse = null;
        this.teamProfileIntroductionResponse = null;
        this.teamMemberIntroductionResponses = null;
        this.historyResponses = null;
    }

    public static TeamProfileResponse teamProfileItems(
            final boolean isTeamProfileEssential,
            final TeamMiniProfileResponse teamMiniProfileResponse,
            final TeamCompletionResponse teamCompletionResponse,
            final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse,
            final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponse,
            final ActivityResponse activityResponse,
            final TeamProfileIntroductionResponse teamProfileIntroductionResponse,
            final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponse,
            final List<HistoryResponse> historyResponse
    ) {
        return new TeamProfileResponse(
                isTeamProfileEssential,
                teamMiniProfileResponse,
                teamCompletionResponse,
                teamProfileTeamBuildingFieldResponse,
                teamMemberAnnouncementResponse,
                activityResponse,
                teamProfileIntroductionResponse,
                teamMemberIntroductionResponse,
                historyResponse
        );
    }
}
