package liaison.linkit.search.presentation.dto.profile;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileListResponseDTO {
    /** 상단 영역: 지금 핫한 공고에요! 6개의 공고 리스트 */
    @Builder.Default
    private List<ProfileResponseDTO.ProfileInformMenu> topCompletionProfiles = new ArrayList<>();

    /** 로필 완성도가 높은 팀원 목록 */
    public static ProfileListResponseDTO of(
            List<ProfileResponseDTO.ProfileInformMenu> topCompletionProfiles) {
        return ProfileListResponseDTO.builder()
                .topCompletionProfiles(topCompletionProfiles)
                .build();
    }
}
