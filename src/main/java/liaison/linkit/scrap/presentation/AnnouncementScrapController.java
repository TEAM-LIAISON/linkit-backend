package liaison.linkit.scrap.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.scrap.business.service.AnnouncementScrapService;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapRequestDTO;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/announcement/scrap")
public class AnnouncementScrapController {

    private final AnnouncementScrapService announcementScrapService;

    @PostMapping("/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<AnnouncementScrapResponseDTO.UpdateAnnouncementScrap> updateAnnouncementScrap(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId,
            @RequestBody final AnnouncementScrapRequestDTO.UpdateAnnouncementScrapRequest updateAnnouncementScrapRequest  // 변경하고자 하는 boolean 상태
    ) {
        return CommonResponse.onSuccess(announcementScrapService.updateAnnouncementScrap(accessor.getMemberId(), teamMemberAnnouncementId, updateAnnouncementScrapRequest));
    }

    @GetMapping
    @MemberOnly
    public CommonResponse<AnnouncementInformMenus> getAnnouncementScraps(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(announcementScrapService.getAnnouncementScraps(accessor.getMemberId()));
    }
}
