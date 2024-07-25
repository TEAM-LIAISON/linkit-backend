package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileRequest;
import liaison.linkit.profile.service.MiniProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private")
@Slf4j
public class MiniProfileController {

    private final MiniProfileService miniProfileService;

    // 미니 프로필 생성/수정 요청
    @PostMapping("/mini-profile")
    @MemberOnly
    public ResponseEntity<Void> createMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart @Valid MiniProfileRequest miniProfileRequest,
            @RequestPart(required = false) MultipartFile miniProfileImage
    ){
        log.info("memberId={}의 미니 프로필 생성 요청이 들어왔습니다.", accessor.getMemberId());
        miniProfileService.save(accessor.getMemberId(), miniProfileRequest, miniProfileImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/mini-profile/update")
    @MemberOnly
    public ResponseEntity<Void> updateMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart @Valid MiniProfileRequest miniProfileRequest,
            @RequestPart(required = false) MultipartFile miniProfileImage
    ) {
        log.info("memberId={}의 미니 프로필 수정 요청이 들어왔습니다.", accessor.getMemberId());
        miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        miniProfileService.update(accessor.getMemberId(), miniProfileRequest, miniProfileImage);
        return ResponseEntity.status(OK).build();
    }
}
