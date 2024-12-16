package liaison.linkit.scrap.business;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.business.mapper.AnnouncementScrapMapper;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.exception.announcementScrap.BadRequestAnnouncementScrapException;
import liaison.linkit.scrap.exception.profileScrap.BadRequestProfileScrapException;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapRequestDTO.UpdateAnnouncementScrapRequest;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.scrap.validation.ScrapValidator;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementScrapService {

    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;

    private final AnnouncementScrapMapper announcementScrapMapper;

    private final ScrapValidator scrapValidator;

    public AnnouncementScrapResponseDTO.UpdateAnnouncementScrap updateAnnouncementScrap(
            final Long memberId,
            final Long teamMemberAnnouncementId,
            final UpdateAnnouncementScrapRequest updateAnnouncementScrapRequest
    ) {

        boolean shouldAddScrap = updateAnnouncementScrapRequest.isChangeScrapValue();

        scrapValidator.validateSelfTeamMemberAnnouncementScrap(memberId, teamMemberAnnouncementId);     // 자기 자신의 프로필 선택에 대한 예외 처리
        scrapValidator.validateMemberMaxTeamMemberAnnouncementScrap(memberId);         // 최대 프로필 스크랩 개수에 대한 예외 처리

        boolean scrapExists = announcementScrapQueryAdapter.existsByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);

        if (scrapExists) {
            handleExistingScrap(memberId, teamMemberAnnouncementId, shouldAddScrap);
        } else {
            handleNonExistingScrap(memberId, teamMemberAnnouncementId, shouldAddScrap);
        }

        return announcementScrapMapper.toUpdateAnnouncementScrap(teamMemberAnnouncementId, shouldAddScrap);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(Long memberId, Long teamMemberAnnouncementId, boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            announcementScrapCommandAdapter.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);
        } else {
            throw BadRequestAnnouncementScrapException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(Long memberId, Long teamMemberAnnouncementId, boolean shouldAddScrap) {
        if (shouldAddScrap) {
            Member member = memberQueryAdapter.findById(memberId);
            TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.findById(teamMemberAnnouncementId);
            AnnouncementScrap announcementScrap = new AnnouncementScrap(null, member, teamMemberAnnouncement);
            announcementScrapCommandAdapter.addAnnouncementScrap(announcementScrap);
        } else {
            throw BadRequestProfileScrapException.EXCEPTION;
        }
    }
}
