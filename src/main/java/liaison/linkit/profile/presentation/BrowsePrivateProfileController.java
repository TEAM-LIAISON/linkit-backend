package liaison.linkit.profile.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.response.browse.BrowsePrivateProfileResponse;
import liaison.linkit.profile.service.BrowsePrivateProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class BrowsePrivateProfileController {

    public final MemberService memberService;
    public final BrowsePrivateProfileService browsePrivateProfileService;

    // 타겟 이력서 열람 컨트롤러
    @GetMapping("/private/profile/{miniProfileId}")
    // 열람 애노테이션 추가
    public ResponseEntity<?> getPrivateProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long miniProfileId
    ) {
        log.info("miniProfileId={}에 대한 내 이력서 열람 요청이 발생했습니다.", miniProfileId);
        try {
            // 1. 열람하고자 하는 내 이력서의 유효성을 판단한다.
            browsePrivateProfileService.validatePrivateProfileByMiniProfile(miniProfileId);

            // 2. 열람하고자 하는 회원의 ID를 가져온다.
            final Long browseTargetMemberId = browsePrivateProfileService.getTargetMemberIdByMiniProfileId(miniProfileId);

            return ResponseEntity.ok().body(browsePrivateProfileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("내 이력서로를 조회하는 과정에서 문제가 발생했습니다.");
        }
    }
}
