package liaison.linkit.wish.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class WishController {
    public final WishService wishService;

    @PostMapping("/wish/private/profile/{miniProfileId}")
    @MemberOnly
    public ResponseEntity<Void> createWishToPrivateProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long miniProfileId
    ) {
        log.info("miniProfileId={} 을 memberId={}가 찜하는 요청이 발생했습니다.", miniProfileId, accessor.getMemberId());
        wishService.createWishToPrivateProfile(accessor.getMemberId(), miniProfileId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
