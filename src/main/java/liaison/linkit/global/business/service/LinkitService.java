package liaison.linkit.global.business.service;

import java.util.List;

import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.member.presentation.dto.MemberDynamicResponse;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import liaison.linkit.profile.presentation.log.dto.ProfileLogDynamicResponse;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.log.TeamLogRepository;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.presentation.announcement.dto.AnnouncementDynamicResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogDynamicResponse;
import liaison.linkit.team.presentation.team.dto.TeamDynamicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LinkitService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final ProfileLogRepository profileLogRepository;
    private final TeamLogRepository teamLogRepository;

    public LinkitDynamicResponseDTO.MemberDynamicListResponse getLinkitProfiles() {
        List<MemberDynamicResponse> memberDynamicRespons =
                memberRepository.findAllDynamicVariablesWithMember();
        return LinkitDynamicResponseDTO.MemberDynamicListResponse.of(memberDynamicRespons);
    }

    public LinkitDynamicResponseDTO.TeamDynamicListResponse getLinkitTeams() {
        List<TeamDynamicResponse> teamDynamicResponses =
                teamRepository.findAllDynamicVariablesWithTeam();
        return LinkitDynamicResponseDTO.TeamDynamicListResponse.of(teamDynamicResponses);
    }

    public LinkitDynamicResponseDTO.AnnouncementDynamicListResponse getLinkitAnnouncements() {
        List<AnnouncementDynamicResponse> announcementDynamicResponses =
                teamMemberAnnouncementRepository
                        .findAllDynamicVariablesWithTeamMemberAnnouncement();
        return LinkitDynamicResponseDTO.AnnouncementDynamicListResponse.of(
                announcementDynamicResponses);
    }

    public LinkitDynamicResponseDTO.ProfileLogDynamicListResponse getLinkitProfileLogs() {
        List<ProfileLogDynamicResponse> profileLogDynamicResponses =
                profileLogRepository.findAllDynamicVariablesWithProfileLog();
        return LinkitDynamicResponseDTO.ProfileLogDynamicListResponse.of(
                profileLogDynamicResponses);
    }

    public LinkitDynamicResponseDTO.TeamLogDynamicListResponse getLinkitTeamLogs() {
        List<TeamLogDynamicResponse> teamLogDynamicResponses =
                teamLogRepository.findAllDynamicVariablesWithTeamLog();
        return LinkitDynamicResponseDTO.TeamLogDynamicListResponse.of(teamLogDynamicResponses);
    }
}
