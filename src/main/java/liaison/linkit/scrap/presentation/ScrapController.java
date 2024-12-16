package liaison.linkit.scrap.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scrap")
@Slf4j
public class ScrapController {

//    // 팀원 공고 스크랩하기
//    @PostMapping("/announcement/{teamMemberAnnouncementId}")
//    @MemberOnly
//    public CommonResponse<AnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap> createScrapToTeamMemberAnnouncement(
//            @Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId
//    ) {
//        scrapValidator.validateMemberMaxTeamMemberAnnouncementScrap(accessor.getMemberId());
//        scrapValidator.validateSelfTeamMemberAnnouncementScrap(accessor.getMemberId(), teamMemberAnnouncementId);
//        return CommonResponse.onSuccess(scrapService.createScrapToTeamMemberAnnouncement(accessor.getMemberId(), teamMemberAnnouncementId));
//    }
//
//    // 팀원 공고 스크랩 취소
//    @DeleteMapping("/announcement/{teamMemberAnnouncementId}")
//    @MemberOnly
//    public CommonResponse<AnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap> cancelScrapToTeamMemberAnnouncement(
//            @Auth final Accessor accessor, @PathVariable final Long teamMemberAnnouncementId
//    ) {
//        return CommonResponse.onSuccess(scrapService.cancelScrapToTeamMemberAnnouncement(accessor.getMemberId(), teamMemberAnnouncementId));
//    }

}
