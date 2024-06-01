package liaison.linkit.team.dto.response;

import liaison.linkit.team.domain.TeamProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamProfileOnBoardingIsValueResponse {
    // 팀 소개서의 희망 팀빌딩 분야
    private final boolean isTeamTeamBuildingField;

    // 활동 방식 & 활동 지역 및 위치
    private final boolean isActivity;

    // 팀원 모집 공고
    private final boolean isTeamMemberAnnouncement;

    // 팀 소개서 미니 프로필 작성 여부
    private final boolean isTeamMiniProfile;

    public static TeamProfileOnBoardingIsValueResponse teamProfileOnBoardingIsValue(
            final TeamProfile teamProfile
    ) {
        return new TeamProfileOnBoardingIsValueResponse(
                teamProfile.getIsTeamTeamBuildingField(),
                teamProfile.getIsActivity(),
                teamProfile.getIsTeamMemberAnnouncement(),
                teamProfile.getIsTeamMiniProfile()
        );
    }

}
