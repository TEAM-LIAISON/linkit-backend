package liaison.linkit.wish.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.wish.dto.response.MyWishResponse;
import liaison.linkit.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class WishController {
    public final WishService wishService;

    // 내 이력서 찜하기
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

    // 팀 소개서 찜하기
    @PostMapping("/wish/team/profile/{teamMiniProfileId}")
    @MemberOnly
    public ResponseEntity<Void> createWishToTeamProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMiniProfileId
    ) {
        log.info("teamMiniProfileId={} 을 memberId={}가 찜하는 요청이 발생했습니다.", teamMiniProfileId, accessor.getMemberId());
        wishService.createWishToTeamProfile(accessor.getMemberId(), teamMiniProfileId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 내가 찜한 목록 조회
    @GetMapping("/my/wish/list")
    @MemberOnly
    public ResponseEntity<Page<MyWishResponse>> getMyWishList(
            @Auth final Accessor accessor,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        log.info("memberId={}의 찜하기 목록을 가져옵니다.", accessor.getMemberId());
        final Page<MyWishResponse> myWishResponsePage = wishService.getMyWishList(accessor.getMemberId(), pageable);
        return ResponseEntity.ok(myWishResponsePage);
    }
}
