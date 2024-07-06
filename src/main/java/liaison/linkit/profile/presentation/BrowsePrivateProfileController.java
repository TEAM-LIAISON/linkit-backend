package liaison.linkit.profile.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.response.browse.BrowsePrivateProfileResponse;
import liaison.linkit.profile.service.ProfileService;
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
    public final ProfileService profileService;

    // 타겟 이력서 열람 메서드 (07.05 추가 예정)
    @GetMapping("/private/profile/{miniProfileId}")
    // 열람 애노테이션 추가
    public ResponseEntity<?> getPrivateProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long miniProfileId
    ) {
        log.info("miniProfileId={}에 대한 내 이력서 열람 요청이 발생했습니다.", miniProfileId);
        try {
            profileService.validatePrivateProfileByMiniProfile(miniProfileId);
            final BrowsePrivateProfileResponse browsePrivateProfileResponse = profileService.getPrivateProfile(miniProfileId);

            return ResponseEntity.ok().body(browsePrivateProfileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("내 이력서로를 조회하는 과정에서 문제가 발생했습니다.");
        }
    }
}
