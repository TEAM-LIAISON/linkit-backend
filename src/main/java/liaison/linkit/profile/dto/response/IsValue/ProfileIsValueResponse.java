package liaison.linkit.profile.dto.response.IsValue;

import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileIsValueResponse {

    // 자기소개 항목
    private final boolean isIntroduction;

    // 기술 항목
    private final boolean isProfileSkill;

    // 희망 팀빌딩 분야 항목
    private final boolean isProfileTeamBuildingField;

    // 지역 및 위치 항목
    private final boolean isProfileRegion;

    // 이력 항목
    private final boolean isAntecedents;

    // 학력 항목
    private final boolean isEducation;

    // 수상 항목
    private final boolean isAwards;

    // 첨부 항목
    private final boolean isAttach;

    // 미니 프로필 항목
    private final boolean isMiniProfile;

    public static ProfileIsValueResponse profileIsValue(final Profile profile) {
        return new ProfileIsValueResponse(
                profile.getIsIntroduction(),
                profile.getIsProfileSkill(),
                profile.getIsProfileTeamBuildingField(),
                profile.getIsProfileRegion(),
                profile.getIsAntecedents(),
                profile.getIsEducation(),
                profile.getIsAwards(),
                profile.getIsAttach(),
                profile.getIsMiniProfile()
        );
    }
}
