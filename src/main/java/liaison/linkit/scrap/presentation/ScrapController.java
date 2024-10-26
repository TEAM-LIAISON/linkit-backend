package liaison.linkit.scrap.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.scrap.business.ScrapService;
import liaison.linkit.scrap.presentation.dto.privateScrap.PrivateScrapResponseDTO.AddPrivateScrap;
import liaison.linkit.scrap.presentation.dto.privateScrap.PrivateScrapResponseDTO.RemovePrivateScrap;
import liaison.linkit.scrap.presentation.dto.teamMemberAnnouncementScrap.TeamMemberAnnouncementScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.AddTeamScrap;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.RemoveTeamScrap;
import liaison.linkit.scrap.validation.ScrapValidator;
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
    public final ScrapValidator scrapValidator;

    // 프로필 스크랩하기
    @PostMapping("/profile/{profileId}")
    @MemberOnly
    public CommonResponse<AddPrivateScrap> createScrapToPrivateProfile(@Auth final Accessor accessor, @PathVariable final Long profileId) {
        scrapValidator.validateMemberMaxPrivateScrap(accessor.getMemberId());
        scrapValidator.validateSelfPrivateScrap(accessor.getMemberId(), profileId);
        return CommonResponse.onSuccess(scrapService.createScrapToPrivateProfile(accessor.getMemberId(), profileId));
    }

    // 팀 스크랩하기
    @PostMapping("/team/{teamId}")
    @MemberOnly
    public CommonResponse<AddTeamScrap> createScrapToTeam(
            @Auth final Accessor accessor,
            @PathVariable final Long teamId
    ) {
        scrapValidator.validateMemberMaxTeamScrap(accessor.getMemberId());
        scrapValidator.validateSelfTeamScrap(accessor.getMemberId(), teamId);
        return CommonResponse.onSuccess(scrapService.createScrapToTeam(accessor.getMemberId(), teamId));
    }

    // 팀원 공고 스크랩하기
    @PostMapping("/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap> createScrapToTeamMemberAnnouncement(
            @Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId
    ) {
        scrapValidator.validateMemberMaxTeamMemberAnnouncementScrap(accessor.getMemberId());
        scrapValidator.validateSelfTeamMemberAnnouncementScrap(accessor.getMemberId(), teamMemberAnnouncementId);
        return CommonResponse.onSuccess(scrapService.createScrapToTeamMemberAnnouncement(accessor.getMemberId(), teamMemberAnnouncementId));
    }

    // 프로필 스크랩 취소
    @DeleteMapping("/profile/{profileId}")
    @MemberOnly
    public CommonResponse<RemovePrivateScrap> removeScrapToPrivateProfile(@Auth final Accessor accessor, @PathVariable final Long profileId) {
        return CommonResponse.onSuccess(scrapService.cancelScrapToPrivateProfile(accessor.getMemberId(), profileId));
    }

    // 팀 스크랩 취소
    @DeleteMapping("/team/{teamId}")
    @MemberOnly
    public CommonResponse<RemoveTeamScrap> cancelScrapToTeamProfile(@Auth final Accessor accessor, @PathVariable final Long teamId) {
        return CommonResponse.onSuccess(scrapService.cancelScrapToTeamProfile(accessor.getMemberId(), teamId));
    }

    // 팀원 공고 스크랩 취소
    @DeleteMapping("/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap> cancelScrapToTeamMemberAnnouncement(
            @Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId
    ) {
        return CommonResponse.onSuccess(scrapService.cancelScrapToTeamMemberAnnouncement(accessor.getMemberId(), teamMemberAnnouncementId));
    }

}
