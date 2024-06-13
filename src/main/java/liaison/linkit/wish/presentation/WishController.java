package liaison.linkit.wish.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {
    public final WishService wishService;

    // "사용자"가 profileId를 찜함. -> "내가 찜한 개인 이력서" 형태로 보여줄 것.
    @PostMapping("/profile/{profileId}")
    @MemberOnly
    public ResponseEntity<Void> createProfileWish(
            @Auth final Accessor accessor,
            @PathVariable final Long profileId
    ) {
        wishService.createProfileWish(accessor.getMemberId(), profileId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // "사용자"가 teamProfileId를 찜함. -> "내가 찜한 팀 소개서" 형태로 보여줄 것.
    @PostMapping("/teamProfile/{teamProfileId}")
    @MemberOnly
    public ResponseEntity<Void> createTeamProfileWish(
            @Auth final Accessor accessor,
            @PathVariable final Long teamProfileId
    ) {
        wishService.createTeamProfileWish(accessor.getMemberId(), teamProfileId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @DeleteMapping("/profile/{profileId}")
//    @MemberOnly
//    public ResponseEntity<Void> deleteProfileWish(
//            @Auth final Accessor accessor,
//            @PathVariable final Long profileId
//    ) {
//        wishService.deleteProfileWish(accessor.getMemberId(), profileId);
//        return ResponseEntity.ok().build();
//    }
}
