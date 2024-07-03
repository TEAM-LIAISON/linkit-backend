package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.profileRegion.ProfileRegionCreateRequest;
import liaison.linkit.profile.service.ProfileRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private")
@Slf4j
public class RegionController {
    private final ProfileRegionService profileRegionService;

    // 1.5.3. 활동 지역 및 위치 생성/수정
    @PostMapping("/region")
    @MemberOnly
    public ResponseEntity<Void> createProfileRegion(
            @Auth final Accessor accessor,
            @RequestBody @Valid ProfileRegionCreateRequest profileRegionCreateRequest
    ) {
        log.info("memberId={}의 프로필 지역 생성 요청이 들어왔습니다.", accessor.getMemberId());
        profileRegionService.save(accessor.getMemberId(), profileRegionCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
