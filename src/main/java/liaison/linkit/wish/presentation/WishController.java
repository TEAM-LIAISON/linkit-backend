package liaison.linkit.wish.presentation;

import java.util.List;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.wish.business.WishService;
import liaison.linkit.wish.presentation.dto.privateWish.PrivateWishResponseDTO;
import liaison.linkit.wish.presentation.dto.response.WishTeamProfileResponse;
import liaison.linkit.wish.presentation.dto.teamWish.TeamWishResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 내 프로필 찜하기
    @PostMapping("/wish/private/profile/{profileId}")
    @MemberOnly
    public CommonResponse<PrivateWishResponseDTO.AddPrivateWish> createWishToPrivateProfile(@Auth final Accessor accessor, @PathVariable final Long profileId) {
        wishService.validateMemberMaxPrivateWish(accessor.getMemberId());
        return CommonResponse.onSuccess(wishService.createWishToPrivateProfile(accessor.getMemberId(), profileId));
    }

    // 팀원 공고 찜하기 (미확정 09.20)
    @PostMapping("/wish/team/profile/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamWishResponseDTO.AddTeamWish> createWishToTeamProfile(@Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId) {
        wishService.validateMemberMaxTeamWish(accessor.getMemberId());
        return CommonResponse.onSuccess(wishService.createWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId));
    }

    // 내 프로필 찜하기 취소
    @DeleteMapping("/wish/private/profile/{profileId}")
    @MemberOnly
    public CommonResponse<PrivateWishResponseDTO.RemovePrivateWish> removeWishToPrivateProfile(@Auth final Accessor accessor, @PathVariable final Long profileId) {
        return CommonResponse.onSuccess(wishService.cancelWishToPrivateProfile(accessor.getMemberId(), profileId));
    }

    // 팀원 공고 찜하기 취소 (미확정 09.20)
    @DeleteMapping("/wish/team/profile/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamWishResponseDTO.RemoveTeamWish> cancelWishToTeamProfile(@Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId) {
        return CommonResponse.onSuccess(wishService.cancelWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId));
    }

    // 내 이력서 찜한 목록 조회 (미확정 09.20)
    @GetMapping("/wish/private/profile/list")
    @MemberOnly
    public ResponseEntity<List<BrowseMiniProfileResponse>> getPrivateProfileWishList(@Auth final Accessor accessor) {
        final List<BrowseMiniProfileResponse> browseMiniProfileResponseList = wishService.getPrivateProfileWishList(accessor.getMemberId());
        return ResponseEntity.ok(browseMiniProfileResponseList);
    }

    // 팀 소개서 찜한 목록 조회 (미확정 09.20)
    @GetMapping("/wish/team/profile/list")
    @MemberOnly
    public ResponseEntity<List<WishTeamProfileResponse>> getTeamProfileWishList(
            @Auth final Accessor accessor
    ) {
        // 찜한 주체의 ID를 전달한다.
        final List<WishTeamProfileResponse> wishTeamProfileResponseList = wishService.getTeamProfileWishList(
                accessor.getMemberId());
        return ResponseEntity.ok(wishTeamProfileResponseList);
    }
}
