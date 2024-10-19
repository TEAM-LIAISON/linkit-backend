package liaison.linkit.wish.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.wish.business.ScrapService;
import liaison.linkit.wish.presentation.dto.privateScrap.PrivateScrapResponseDTO.AddPrivateScrap;
import liaison.linkit.wish.presentation.dto.privateScrap.PrivateScrapResponseDTO.RemovePrivateScrap;
import liaison.linkit.wish.presentation.dto.teamScrap.TeamScrapResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scrap")
@Slf4j
public class ScrapController {

    public final ScrapService scrapService;

    // 프로필 찜하기
    @PostMapping("/private/profile/{profileId}")
    @MemberOnly
    public CommonResponse<AddPrivateScrap> createWishToPrivateProfile(@Auth final Accessor accessor, @PathVariable final Long profileId) {
        scrapService.validateMemberMaxPrivateWish(accessor.getMemberId());
        return CommonResponse.onSuccess(scrapService.createWishToPrivateProfile(accessor.getMemberId(), profileId));
    }

    // 팀 찜하기
    @PostMapping("/team/{teamId}")
    @MemberOnly
    public CommonResponse<TeamScrapResponseDTO.AddTeamWish> createWishToTeam(@Auth final Accessor accessor, @PathVariable final Long teamId) {
        scrapService.validateMemberMaxTeamWish(accessor.getMemberId());
        return CommonResponse.onSuccess(scrapService.createWishToTeam(accessor.getMemberId(), teamId));
    }


    // 팀원 공고 찜하기 (미확정 09.20)
    @PostMapping("/wish/team/profile/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamScrapResponseDTO.AddTeamWish> createWishToTeamProfile(@Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId) {

        scrapService.validateMemberMaxTeamWish(accessor.getMemberId());
        return CommonResponse.onSuccess(scrapService.createWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId));
    }

    // 내 프로필 찜하기 취소
    @DeleteMapping("/private/profile/{profileId}")
    @MemberOnly
    public CommonResponse<RemovePrivateScrap> removeWishToPrivateProfile(@Auth final Accessor accessor, @PathVariable final Long profileId) {
        return CommonResponse.onSuccess(scrapService.cancelWishToPrivateProfile(accessor.getMemberId(), profileId));
    }

    // 팀원 공고 찜하기 취소 (미확정 09.20)
    @DeleteMapping("/wish/team/profile/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamScrapResponseDTO.RemoveTeamWish> cancelWishToTeamProfile(@Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId) {
        return CommonResponse.onSuccess(scrapService.cancelWishToTeamProfile(accessor.getMemberId(), teamMemberAnnouncementId));
    }

}
