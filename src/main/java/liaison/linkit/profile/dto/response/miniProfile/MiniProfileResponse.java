package liaison.linkit.profile.dto.response.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MiniProfileResponse {

    private final Long id;
    private final String profileTitle;
    private final String miniProfileImg;
    private final Boolean isActivate;
    private final List<String> myKeywordNames;
    private final String memberName;
    private final List<String> jobRoleNames;

    public MiniProfileResponse(final String memberName, final List<String> jobRoleNames) {
        this.id = null;
        this.profileTitle = null;
        this.miniProfileImg = null;
        this.isActivate = null;
        this.myKeywordNames = null;
        this.memberName = memberName;
        this.jobRoleNames = jobRoleNames;
    }

    public static MiniProfileResponse personalMiniProfile(
            final MiniProfile miniProfile,
            final List<MiniProfileKeyword> miniProfileKeywords,
            final String memberName,
            final List<String> jobRoleNames
    ) {
        List<String> myKeywordNames = miniProfileKeywords.stream()
                .map(MiniProfileKeyword::getMyKeywordNames) // 올바른 메서드 참조 사용
                .collect(Collectors.toList());

        return new MiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getMiniProfileImg(),
                miniProfile.isActivate(),
                myKeywordNames,
                memberName,
                jobRoleNames
        );
    }


}
