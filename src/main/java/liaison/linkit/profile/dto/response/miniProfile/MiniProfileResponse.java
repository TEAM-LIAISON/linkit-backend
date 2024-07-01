package liaison.linkit.profile.dto.response.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MiniProfileResponse {

    private final String profileTitle;
    private final LocalDate uploadPeriod;
    private final Boolean uploadDeadline;
    private final String miniProfileImg;
    private final String myValue;
    private final List<String> myKeywordNames;

    public MiniProfileResponse() {
        this.profileTitle = null;
        this.uploadPeriod = null;
        this.uploadDeadline = null;
        this.miniProfileImg = null;
        this.myValue = null;
        this.myKeywordNames = null;
    }

    public static MiniProfileResponse of(final MiniProfile miniProfile) {
        final List<String> keywordNames = miniProfile.getMiniProfileKeywordList().stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .toList();

        return new MiniProfileResponse(
                miniProfile.getProfileTitle(),
                miniProfile.getUploadPeriod(),
                miniProfile.isUploadDeadline(),
                miniProfile.getMiniProfileImg(),
                miniProfile.getMyValue(),
                keywordNames
        );
    }

    public static MiniProfileResponse personalMiniProfile(final MiniProfile miniProfile) {

        final List<String> keywordNames = miniProfile.getMiniProfileKeywordList().stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .toList();

        return new MiniProfileResponse(
                miniProfile.getProfileTitle(),
                miniProfile.getUploadPeriod(),
                miniProfile.isUploadDeadline(),
                miniProfile.getMiniProfileImg(),
                miniProfile.getMyValue(),
                keywordNames
        );
    }


}
