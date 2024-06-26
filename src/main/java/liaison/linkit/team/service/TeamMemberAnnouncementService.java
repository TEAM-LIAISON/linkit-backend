package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberAnnouncementService {

    private final TeamProfileRepository teamProfileRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    // 회원에 대한 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    public void validateTeamMemberAnnouncement(final Long memberId) {
        if (!teamMemberAnnouncementRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(INVALID_TEAM_MEMBER_ANNOUNCEMENT_WITH_PROFILE);
        }
    }

    @Transactional(readOnly = true)
    public List<TeamMemberAnnouncementResponse> getTeamMemberAnnouncement(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final List<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByTeamProfileId(teamProfile.getId());
        return teamMemberAnnouncements.stream()
                .map(this::getTeamMemberAnnouncementResponse)
                .toList();
    }

    private TeamMemberAnnouncementResponse getTeamMemberAnnouncementResponse(
            final TeamMemberAnnouncement teamMemberAnnouncement
    ) {
        return TeamMemberAnnouncementResponse.of(teamMemberAnnouncement);
    }
}
