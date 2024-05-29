package liaison.linkit.wish.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.wish.dto.request.WishProfileCreateRequest;
import liaison.linkit.wish.dto.request.WishTeamProfileCreateRequest;
import liaison.linkit.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {
    public final WishService wishService;

    // "사용자"가 profileId를 찜함. -> "내가 찜한 개인 이력서" 형태로 보여줄 것.
    @PostMapping("/{profileId}")
    @MemberOnly
    public ResponseEntity<Void> createProfileWish(
            @Auth final Accessor accessor,
            @RequestBody @Valid WishProfileCreateRequest wishProfileCreateRequest
    ) {
        wishService.createProfileWish(accessor.getMemberId(), wishProfileCreateRequest);
        return ResponseEntity.ok().build();
    }

    // "사용자"가 teamProfileId를 찜함. -> "내가 찜한 팀 소개서" 형태로 보여줄 것.
    @PostMapping("/{teamProfileId}")
    @MemberOnly
    public ResponseEntity<Void> createTeamProfileWish(
            @Auth final Accessor accessor,
            @RequestBody @Valid WishTeamProfileCreateRequest wishTeamProfileCreateRequest
    ) {
        wishService.createTeamProfileWish(accessor.getMemberId(), wishTeamProfileCreateRequest);
        return ResponseEntity.ok().build();
    }

    // RUD -> Update 필요? None

    // RD만 구현하기 -> R의 경우 전체 조회와 개별 조회 모두 만들어야 하나?
    // Delete -> 전체 삭제와 개별 삭제 모두 만들기

}
