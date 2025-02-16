package liaison.linkit.search.presentation.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfileSearchResponseDTO {

    /**
     * 상단 영역: 프로필 완성도가 높은 팀원 6명의 리스트
     */
    @Builder.Default
    private List<ProfileInformMenu> topCompletionProfiles = new ArrayList<>();

    /**
     * 하단 영역: 상단에 포함된 프로필을 제외한 나머지 팀원들의 페이지네이션 결과
     */
    private Page<ProfileInformMenu> defaultProfiles;
}
