package liaison.linkit.scrap.business;

import java.time.LocalDateTime;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberBasicInformQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.scrap.business.mapper.ProfileScrapMapper;
import liaison.linkit.scrap.business.mapper.AnnouncementScrapMapper;
import liaison.linkit.scrap.business.mapper.TeamScrapMapper;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapQueryAdapter;
import liaison.linkit.scrap.implement.teamMemberAnnouncement.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamMemberAnnouncement.TeamMemberAnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapService {

    private final MemberQueryAdapter memberQueryAdapter;

    private final MemberBasicInformQueryAdapter memberBasicInformQueryAdapter;

    private final ProfileScrapMapper profileScrapMapper;
    private final ProfileScrapQueryAdapter profileScrapQueryAdapter;
    private final ProfileScrapCommandAdapter profileScrapCommandAdapter;

    private final TeamScrapMapper teamScrapMapper;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final TeamScrapCommandAdapter teamScrapCommandAdapter;

    private final AnnouncementScrapMapper announcementScrapMapper;
    private final TeamMemberAnnouncementScrapQueryAdapter teamMemberAnnouncementScrapQueryAdapter;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;

    private final ProfileQueryAdapter profileQueryAdapter;

    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;


    // 팀원 공고 스크랩 메서드
    public AddTeamMemberAnnouncementScrap createScrapToTeamMemberAnnouncement(final Long memberId, final Long teamMemberAnnouncementId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.findById(teamMemberAnnouncementId);

        final AnnouncementScrap createdAnnouncementScrap = announcementScrapCommandAdapter.create(
                new AnnouncementScrap(null, member, teamMemberAnnouncement, LocalDateTime.now()));

        return announcementScrapMapper.toAddTeamMemberAnnouncementScrap(createdAnnouncementScrap);

    }


    // 팀원 공고 스크랩 취소 메서드
    public AnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap cancelScrapToTeamMemberAnnouncement(final Long memberId, final Long teamMemberAnnouncementId) {

        announcementScrapCommandAdapter.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);

        final Member member = memberQueryAdapter.findById(memberId);
        member.subTeamMemberAnnouncementScrapCount();

        return announcementScrapMapper.toRemoveTeamMemberAnnouncementScrap();

    }
}
