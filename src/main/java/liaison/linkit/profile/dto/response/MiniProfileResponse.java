package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.MiniProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class MiniProfileResponse {

    private final String profileTitle;
    private final LocalDate uploadPeriod;
    private final Boolean uploadDeadline;
    private final String miniProfileImg;
    private final String myValue;
    private final String skillSets;

    public MiniProfileResponse() {
        this.profileTitle = null;
        this.uploadPeriod = null;
        this.uploadDeadline = null;
        this.miniProfileImg = null;
        this.myValue = null;
        this.skillSets = null;
    }

    public static MiniProfileResponse of(final MiniProfile miniProfile) {
        return new MiniProfileResponse(
                miniProfile.getProfileTitle(),
                miniProfile.getUploadPeriod(),
                miniProfile.isUploadDeadline(),
                miniProfile.getMiniProfileImg(),
                miniProfile.getMyValue(),
                miniProfile.getSkillSets()
        );
    }

    public static MiniProfileResponse personalMiniProfile(final MiniProfile miniProfile) {
        return new MiniProfileResponse(
                miniProfile.getProfileTitle(),
                miniProfile.getUploadPeriod(),
                miniProfile.isUploadDeadline(),
                miniProfile.getMiniProfileImg(),
                miniProfile.getMyValue(),
                miniProfile.getSkillSets()
        );
    }


}
