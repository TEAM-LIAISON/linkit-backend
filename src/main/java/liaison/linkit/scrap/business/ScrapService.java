package liaison.linkit.scrap.business;

import java.time.LocalDateTime;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberBasicInformQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.scrap.business.mapper.PrivateScrapMapper;
import liaison.linkit.scrap.business.mapper.TeamMemberAnnouncementScrapMapper;
import liaison.linkit.scrap.business.mapper.TeamScrapMapper;
import liaison.linkit.scrap.domain.PrivateScrap;
import liaison.linkit.scrap.domain.TeamMemberAnnouncementScrap;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.exception.teamScrap.ForbiddenTeamScrapException;
import liaison.linkit.scrap.implement.privateScrap.PrivateScrapCommandAdapter;
import liaison.linkit.scrap.implement.privateScrap.PrivateScrapQueryAdapter;
import liaison.linkit.scrap.implement.teamMemberAnnouncement.TeamMemberAnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamMemberAnnouncement.TeamMemberAnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.privateScrap.PrivateScrapResponseDTO.AddPrivateScrap;
import liaison.linkit.scrap.presentation.dto.privateScrap.PrivateScrapResponseDTO.RemovePrivateScrap;
import liaison.linkit.scrap.presentation.dto.teamMemberAnnouncementScrap.TeamMemberAnnouncementScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.teamMemberAnnouncementScrap.TeamMemberAnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.AddTeamScrap;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.RemoveTeamScrap;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.implement.teamMemberAnnouncement.TeamMemberAnnouncementQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapService {

    private final MemberQueryAdapter memberQueryAdapter;

    private final MemberBasicInformQueryAdapter memberBasicInformQueryAdapter;

    private final PrivateScrapMapper privateScrapMapper;
    private final PrivateScrapQueryAdapter privateScrapQueryAdapter;
    private final PrivateScrapCommandAdapter privateScrapCommandAdapter;

    private final TeamScrapMapper teamScrapMapper;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final TeamScrapCommandAdapter teamScrapCommandAdapter;

    private final TeamMemberAnnouncementScrapMapper teamMemberAnnouncementScrapMapper;
    private final TeamMemberAnnouncementScrapQueryAdapter teamMemberAnnouncementScrapQueryAdapter;
    private final TeamMemberAnnouncementScrapCommandAdapter teamMemberAnnouncementScrapCommandAdapter;

    private final ProfileQueryAdapter profileQueryAdapter;

    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;


    // 프로필 스크랩 메서드
    public AddPrivateScrap createScrapToPrivateProfile(final Long memberId, final Long profileId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final Profile profile = profileQueryAdapter.findById(profileId);

        final PrivateScrap createdPrivateScrap = privateScrapCommandAdapter.create(new PrivateScrap(null, member, profile, LocalDateTime.now()));
        member.addPrivateScrapCount();

        return privateScrapMapper.toAddPrivateScrap(createdPrivateScrap);

    }

    // 팀 스크랩 메서드
    public AddTeamScrap createScrapToTeam(final Long memberId, final Long teamId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final Team team = teamQueryAdapter.findById(teamId);

        // 팀 소속 여부
        if (teamMemberQueryAdapter.existsTeamMemberByMemberIdAndTeamId(memberId, teamId)) {
            throw ForbiddenTeamScrapException.EXCEPTION;
        }

        final TeamScrap createdTeamScrap = teamScrapCommandAdapter.create(new TeamScrap(null, member, team, LocalDateTime.now()));
        member.addTeamScrapCount();

        return teamScrapMapper.toAddTeamScrap(createdTeamScrap);

    }

    // 팀원 공고 스크랩 메서드
    public AddTeamMemberAnnouncementScrap createScrapToTeamMemberAnnouncement(final Long memberId, final Long teamMemberAnnouncementId) {

        final Member member = memberQueryAdapter.findById(memberId);
        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.findById(teamMemberAnnouncementId);

        final TeamMemberAnnouncementScrap createdTeamMemberAnnouncementScrap = teamMemberAnnouncementScrapCommandAdapter.create(
                new TeamMemberAnnouncementScrap(null, member, teamMemberAnnouncement, LocalDateTime.now()));

        return teamMemberAnnouncementScrapMapper.toAddTeamMemberAnnouncementScrap(createdTeamMemberAnnouncementScrap);

    }

    // 프로필 찜하기 취소 메서드
    public RemovePrivateScrap cancelScrapToPrivateProfile(final Long memberId, final Long profileId) {

        privateScrapCommandAdapter.deleteByMemberIdAndProfileId(memberId, profileId);

        final Member member = memberQueryAdapter.findById(memberId);
        member.subPrivateScrapCount();

        return privateScrapMapper.toRemovePrivateScrap();

    }

    // 팀 스크랩 취소 메서드
    public RemoveTeamScrap cancelScrapToTeamProfile(final Long memberId, final Long teamId) {

        teamScrapCommandAdapter.deleteByMemberIdAndTeamId(memberId, teamId);

        final Member member = memberQueryAdapter.findById(memberId);
        member.subTeamScrapCount();

        return teamScrapMapper.toRemoveTeamScrap();

    }

    // 팀원 공고 스크랩 취소 메서드
    public TeamMemberAnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap cancelScrapToTeamMemberAnnouncement(final Long memberId, final Long teamMemberAnnouncementId) {

        teamMemberAnnouncementScrapCommandAdapter.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);

        final Member member = memberQueryAdapter.findById(memberId);
        member.subTeamMemberAnnouncementScrapCount();

        return teamMemberAnnouncementScrapMapper.toRemoveTeamMemberAnnouncementScrap();

    }
}
