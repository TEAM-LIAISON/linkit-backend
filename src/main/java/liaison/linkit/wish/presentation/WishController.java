package liaison.linkit.wish.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.wish.dto.response.WishTeamProfileResponse;
import liaison.linkit.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        wishService.validateMemberMaxPrivateWish(accessor.getMemberId());
        // 최대 개수를 넘지 않았다면 아래가 실행된다.
        wishService.createWishToPrivateProfile(accessor.getMemberId(), miniProfileId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 팀 소개서 찜하기
    @PostMapping("/wish/team/profile/{teamMemberAnnouncementId}")
    @MemberOnly
    public ResponseEntity<Void> createWishToTeamProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId
    ) {
        log.info("teamMemberAnnouncementId={} 을 memberId={}가 찜하는 요청이 발생했습니다.", teamMemberAnnouncementId, accessor.getMemberId());
        wishService.validateMemberMaxTeamWish(accessor.getMemberId());

        // 최대 개수를 넘지 않았다면 아래가 실행된다.
        wishService.createWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // 내 이력서 찜하기 취소
    @DeleteMapping("/wish/private/profile/{miniProfileId}")
    @MemberOnly
    public ResponseEntity<Void> cancelWishToPrivateProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long miniProfileId
    ) {
        log.info("miniProfileId={} 을 memberId={}가 찜 취소하는 요청이 발생했습니다.", miniProfileId, accessor.getMemberId());
        wishService.cancelWishToPrivateProfile(accessor.getMemberId(), miniProfileId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 팀 소개서 찜하기 취소
    @DeleteMapping("/wish/team/profile/{teamMemberAnnouncementId}")
    @MemberOnly
    public ResponseEntity<Void> cancelWishToTeamProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId
    ) {
        log.info("teamMemberAnnouncementId={} 을 memberId={}가 찜하는 요청이 발생했습니다.", teamMemberAnnouncementId, accessor.getMemberId());
        wishService.cancelWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 내 이력서 찜한 목록 조회
    @GetMapping("/wish/private/profile/list")
    @MemberOnly
    public ResponseEntity<List<MiniProfileResponse>> getPrivateProfileWishList(
            @Auth final Accessor accessor
    ) {
        log.info("memberId={}의 내 이력서 찜하기 목록을 가져옵니다.", accessor.getMemberId());
        final List<MiniProfileResponse> miniProfileResponseList = wishService.getPrivateProfileWishList(accessor.getMemberId());
        return ResponseEntity.ok(miniProfileResponseList);
    }

    // 팀 소개서 찜한 목록 조회
    @GetMapping("/wish/team/profile/list")
    @MemberOnly
    public ResponseEntity<List<WishTeamProfileResponse>> getTeamProfileWishList(
            @Auth final Accessor accessor
    ) {
        log.info("memberId={}의 팀 소개서 찜하기 목록을 가져옵니다.", accessor.getMemberId());
        final List<WishTeamProfileResponse> wishTeamProfileResponseList = wishService.getTeamProfileWishList(accessor.getMemberId());
        return ResponseEntity.ok(wishTeamProfileResponseList);
    }
}
