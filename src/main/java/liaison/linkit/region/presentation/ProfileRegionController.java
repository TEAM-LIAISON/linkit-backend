package liaison.linkit.region.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.region.dto.request.ProfileRegionCreateRequest;
import liaison.linkit.region.service.ProfileRegionService;
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
@RequestMapping("/profile_region")
@Slf4j
public class ProfileRegionController {
    private final ProfileRegionService profileRegionService;

    // 프로필 지역 생성
    @PostMapping
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
