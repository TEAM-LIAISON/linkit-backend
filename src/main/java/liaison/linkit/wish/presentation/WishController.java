package liaison.linkit.wish.presentation;

import java.util.List;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.wish.presentation.dto.privateWish.PrivateWishResponseDTO;
import liaison.linkit.wish.presentation.dto.privateWish.PrivateWishResponseDTO.AddPrivateWish;
import liaison.linkit.wish.presentation.dto.response.WishTeamProfileResponse;
import liaison.linkit.wish.presentation.dto.teamWish.TeamWishResponseDTO;
import liaison.linkit.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/wish/private/profile/{profileId}")
    @MemberOnly
    public CommonResponse<PrivateWishResponseDTO.AddPrivateWish> createWishToPrivateProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long profileId
    ) {
        wishService.validateMemberMaxPrivateWish(accessor.getMemberId());
        return CommonResponse.onSuccess(wishService.createWishToPrivateProfile(accessor.getMemberId(), profileId));
    }

    @PostMapping("/wish/team/profile/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamWishResponseDTO.AddTeamWish> createWishToTeamProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId
    ) {
        wishService.validateMemberMaxTeamWish(accessor.getMemberId());
        return CommonResponse.onSuccess(
                wishService.createWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId));
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
        log.info("teamMemberAnnouncementId={} 을 memberId={}가 찜하는 요청이 발생했습니다.", teamMemberAnnouncementId,
                accessor.getMemberId());
        wishService.cancelWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 내 이력서 찜한 목록 조회
    @GetMapping("/wish/private/profile/list")
    @MemberOnly
    public ResponseEntity<List<BrowseMiniProfileResponse>> getPrivateProfileWishList(
            @Auth final Accessor accessor
    ) {
        log.info("memberId={}의 내 이력서 찜하기 목록을 가져옵니다.", accessor.getMemberId());
        // 찜한 주체의 ID를 전달한다.
        final List<BrowseMiniProfileResponse> browseMiniProfileResponseList = wishService.getPrivateProfileWishList(
                accessor.getMemberId());
        return ResponseEntity.ok(browseMiniProfileResponseList);
    }

    // 팀 소개서 찜한 목록 조회
    @GetMapping("/wish/team/profile/list")
    @MemberOnly
    public ResponseEntity<List<WishTeamProfileResponse>> getTeamProfileWishList(
            @Auth final Accessor accessor
    ) {
        log.info("memberId={}의 팀 소개서 찜하기 목록을 가져옵니다.", accessor.getMemberId());
        // 찜한 주체의 ID를 전달한다.
        final List<WishTeamProfileResponse> wishTeamProfileResponseList = wishService.getTeamProfileWishList(
                accessor.getMemberId());
        return ResponseEntity.ok(wishTeamProfileResponseList);
    }
}
