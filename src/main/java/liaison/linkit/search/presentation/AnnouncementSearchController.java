package liaison.linkit.search.presentation;

import liaison.linkit.search.service.AnnouncementSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/announcement/search")
@Slf4j
public class AnnouncementSearchController {

    private final AnnouncementSearchService announcementSearchService;

    /**
     * 공고 검색 엔드포인트
     *
     * @param majorPosition 포지션 대분류 (선택적)
     * @param skillName     보유 스킬 (선택적)
     * @param cityName      활동 지역 (선택적)
     * @param               규모 (선택적)
     * @param page          페이지 번호 (기본값: 0)
     * @param size          페이지 크기 (기본값: 20)
     * @return 팀원 목록과 페이지 정보
     */

}
