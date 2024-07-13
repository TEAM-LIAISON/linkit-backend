package liaison.linkit.search.presentation;

import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.search.dto.response.SearchTeamProfileResponse;
import liaison.linkit.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
// 팀 찾기 및 팀원 찾기 컨트롤러
public class SearchController {

    public final SearchService searchService;

    // 팀원 찾기 구현부
    @GetMapping("/search/private/profile")
    public ResponseEntity<Page<MiniProfileResponse>> findPrivateMiniProfile(
            @PageableDefault(size = 10) final Pageable pageable,
            // 필터링 항목 1. 팀빌딩 분야
            @RequestParam(required = false) final List<String> teamBuildingFieldName,
            // 필터링 항목 2. 희망 역할
            @RequestParam(required = false) final List<String> jobRoleName,
            // 필터링 항목 3. 보유 기술
            @RequestParam(required = false) final List<String> skillName,
            // 필터링 항목 4. 활동 지역 (시/도)
            @RequestParam(required = false) final String cityName,
            // 필터링 항목 4. 활동 지역 (시/군/구)
            @RequestParam(required = false) final String divisionName
    ) {
        log.info("팀원 찾기 요청이 들어왔습니다.");

        final Page<MiniProfileResponse> privateMiniProfiles = searchService.findPrivateMiniProfile(
                pageable,
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName
        );
        log.info("privateMiniProfiles={}",privateMiniProfiles);
        return ResponseEntity.ok(privateMiniProfiles);
    }

    @GetMapping("/search/team/profile")
    public ResponseEntity<Page<SearchTeamProfileResponse>> findSearchTeamProfile(
            @PageableDefault(size = 10) final Pageable pageable,
            // 필터링 항목 1. 희망 팀빌딩 분야
            @RequestParam(required = false) final List<String> teamBuildingFieldName,
            // 필터링 항목 2. 직무 및 역할
            @RequestParam(required = false) final List<String> jobRoleName,
            // 필터링 항목 3. 요구 역량
            @RequestParam(required = false) final List<String> skillName,
            // 필터링 항목 4. 활동 지역 (시/도)
            @RequestParam(required = false) final String cityName,
            // 필터링 항목 4. 활동 지역 (시/군/구)
            @RequestParam(required = false) final String divisionName,
            // 필터링 항목 5. 활동 방식
            @RequestParam(required = false) final List<String> activityTagName
    ) {
        log.info("팀 찾기(팀원 공고) 요청이 들어왔습니다.");
        final Page<SearchTeamProfileResponse> searchTeamProfileResponsePage = searchService.findTeamMemberAnnouncementsWithTeamMiniProfile(
                pageable,
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                activityTagName
        );
        return ResponseEntity.ok(searchTeamProfileResponsePage);
    }

}
