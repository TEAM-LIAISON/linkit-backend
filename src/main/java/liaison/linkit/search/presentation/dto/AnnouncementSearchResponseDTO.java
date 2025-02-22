package liaison.linkit.search.presentation.dto;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
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
public class AnnouncementSearchResponseDTO {

    /** 상단 영역: 지금 핫한 공고에요! 6개의 공고 리스트 */
    @Builder.Default private List<AnnouncementInformMenu> hotAnnouncements = new ArrayList<>();

    /** 하단 영역: 상단에 포함된 공고를 제외한 나머지 공고들의 페이지네이션 결과 */
    private Page<AnnouncementInformMenu> defaultAnnouncements;
}
