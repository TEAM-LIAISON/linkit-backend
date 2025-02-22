package liaison.linkit.matching.business.assembler.common;

import java.util.List;

import liaison.linkit.matching.business.mapper.MatchingMapper;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverAnnouncementInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.assembler.common.AnnouncementCommonAssembler;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingCommonAssembler {

    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;

    // Mapper
    private final MatchingMapper matchingMapper;

    // Assemblers
    private final ProfileInformMenuAssembler profileInformMenuAssembler;
    private final AnnouncementCommonAssembler announcementCommonAssembler;

    // ─── 헬퍼 메서드: 발신자/수신자 정보 조립 ──────────────────────────

    public SenderProfileInformation assembleSenderProfileInformation(final String senderEmailId) {
        Profile profile = profileQueryAdapter.findByEmailId(senderEmailId);
        ProfilePositionDetail positionDetail =
                profileInformMenuAssembler.assembleProfilePositionDetail(profile);
        return matchingMapper.toSenderProfileInformation(profile, positionDetail);
    }

    public SenderTeamInformation assembleSenderTeamInformation(final String senderTeamCode) {
        Team team = teamQueryAdapter.findByTeamCode(senderTeamCode);
        TeamScaleItem scaleItem = announcementCommonAssembler.fetchTeamScaleItem(team);
        return matchingMapper.toSenderTeamInformation(team, scaleItem);
    }

    public ReceiverProfileInformation assembleReceiverProfileInformation(
            final String receiverEmailId) {
        Profile profile = profileQueryAdapter.findByEmailId(receiverEmailId);
        ProfilePositionDetail positionDetail =
                profileInformMenuAssembler.assembleProfilePositionDetail(profile);
        return matchingMapper.toReceiverProfileInformation(profile, positionDetail);
    }

    public ReceiverTeamInformation assembleReceiverTeamInformation(final String receiverTeamCode) {
        Team team = teamQueryAdapter.findByTeamCode(receiverTeamCode);
        TeamScaleItem scaleItem = announcementCommonAssembler.fetchTeamScaleItem(team);
        return matchingMapper.toReceiverTeamInformation(team, scaleItem);
    }

    public ReceiverAnnouncementInformation assembleReceiverAnnouncementInformation(
            final Long receiverAnnouncementId) {
        TeamMemberAnnouncement announcement =
                teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                        receiverAnnouncementId);
        AnnouncementPositionItem positionItem =
                announcementCommonAssembler.fetchAnnouncementPositionItem(announcement);
        List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> skillNames =
                announcementCommonAssembler.fetchAnnouncementSkills(announcement);
        return matchingMapper.toReceiverAnnouncementInformation(
                announcement, positionItem, skillNames);
    }
}
