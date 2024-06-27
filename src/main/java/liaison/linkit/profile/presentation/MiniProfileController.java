package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileCreateRequest;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileUpdateRequest;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.service.MiniProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mini-profile")
@Slf4j
public class MiniProfileController {

    private final MiniProfileService miniProfileService;

    // 미니 프로필 조회 요청
    @GetMapping
    @MemberOnly
    public ResponseEntity<MiniProfileResponse> getMiniProfile(
            @Auth final Accessor accessor
    ) {
        miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        final MiniProfileResponse miniProfileResponse = miniProfileService.getPersonalMiniProfile(accessor.getMemberId());
        return ResponseEntity.ok().body(miniProfileResponse);
    }

    // 미니 프로필 생성 요청
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart @Valid MiniProfileCreateRequest miniProfileCreateRequest,
            @RequestPart(required = false) MultipartFile miniProfileImage
    ){
        log.info("memberId={}의 미니 프로필 생성 요청이 들어왔습니다.", accessor.getMemberId());
        miniProfileService.save(accessor.getMemberId(), miniProfileCreateRequest, miniProfileImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 미니 프로필 항목 수정
    @PatchMapping
    @MemberOnly
    public ResponseEntity<Void> updateMiniProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final MiniProfileUpdateRequest miniProfileUpdateRequest
    ){
        miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        miniProfileService.update(accessor.getMemberId(), miniProfileUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    // 미니 프로필 항목 삭제
    @DeleteMapping
    @MemberOnly
    public ResponseEntity<Void> deleteMiniProfile(@Auth final Accessor accessor) {
        miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        miniProfileService.delete(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
