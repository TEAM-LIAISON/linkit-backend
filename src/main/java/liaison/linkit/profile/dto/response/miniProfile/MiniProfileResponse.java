package liaison.linkit.profile.dto.response.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MiniProfileResponse {

    private final Long id;
    private final String profileTitle;
    private final LocalDate uploadPeriod;
    private final Boolean uploadDeadline;
    private final String miniProfileImg;
    private final String myValue;
    private final List<String> myKeywordNames;

    public MiniProfileResponse() {
        this.id = null;
        this.profileTitle = null;
        this.uploadPeriod = null;
        this.uploadDeadline = null;
        this.miniProfileImg = null;
        this.myValue = null;
        this.myKeywordNames = null;
    }

    public static MiniProfileResponse personalMiniProfile(final MiniProfile miniProfile, final List<MiniProfileKeyword> miniProfileKeywords) {

        List<String> myKeywordNames = miniProfileKeywords.stream()
                .map(MiniProfileKeyword::getMyKeywordNames) // 올바른 메서드 참조 사용
                .collect(Collectors.toList());

        return new MiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getUploadPeriod(),
                miniProfile.isUploadDeadline(),
                miniProfile.getMiniProfileImg(),
                miniProfile.getMyValue(),
                myKeywordNames
        );
    }


}
