package liaison.linkit.matching.business.resolver;

import java.util.List;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.util.RegionUtil;
import liaison.linkit.matching.business.assembler.common.MatchingCommonAssembler;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.assembler.common.AnnouncementCommonAssembler;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingInfoResolver {

    // Adapters
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;

    // Assemblers
    private final AnnouncementCommonAssembler announcementCommonAssembler;
    private final MatchingCommonAssembler matchingCommonAssembler;

    public Object resolveSenderInfo(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            return matchingCommonAssembler.assembleSenderProfileInformation(
                    matching.getSenderEmailId());
        } else {
            return matchingCommonAssembler.assembleSenderTeamInformation(
                    matching.getSenderTeamCode());
        }
    }

    public Object resolveReceiverInfo(final Matching matching) {
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            return matchingCommonAssembler.assembleReceiverProfileInformation(
                    matching.getReceiverEmailId());
        } else if (matching.getReceiverType() == ReceiverType.TEAM) {
            return matchingCommonAssembler.assembleReceiverTeamInformation(
                    matching.getReceiverTeamCode());
        } else if (matching.getReceiverType() == ReceiverType.ANNOUNCEMENT) {
            return matchingCommonAssembler.assembleReceiverAnnouncementInformation(
                    matching.getReceiverAnnouncementId());
        }
        return null;
    }

    // 발신자 이름 조회 헬퍼 메서드
    public String getSenderName(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            Member member = memberQueryAdapter.findByEmailId(matching.getSenderEmailId());
            return member.getMemberBasicInform().getMemberName();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            return team.getTeamName();
        }
    }

    // 수신자 이름 조회 헬퍼 메서드
    public String getReceiverName(Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Member member = memberQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                return member.getMemberBasicInform().getMemberName();
            }
            case TEAM -> {
                Team team = teamQueryAdapter.findByTeamCode(matching.getReceiverTeamCode());
                return team.getTeamName();
            }
            case ANNOUNCEMENT -> {
                TeamMemberAnnouncement announcement =
                        teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                                matching.getReceiverAnnouncementId());
                return announcement.getTeam().getTeamName();
            }
            default -> throw new IllegalStateException(
                    "Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 수신자 memberId 조회 헬퍼 메서드
    public Long getReceiverMemberId(final Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                return profile.getMember().getId();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode =
                        matching.getReceiverType() == ReceiverType.TEAM
                                ? matching.getReceiverTeamCode()
                                : teamMemberAnnouncementQueryAdapter
                                        .getTeamMemberAnnouncement(
                                                matching.getReceiverAnnouncementId())
                                        .getTeam()
                                        .getTeamCode();
                return teamMemberQueryAdapter.findTeamOwnerByTeamCode(teamCode).getId();
            }
            default -> throw new IllegalStateException(
                    "Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    public Long getSenderMemberId(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            return memberQueryAdapter.findByEmailId(matching.getSenderEmailId()).getId();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            return teamMemberQueryAdapter.getTeamOwnerMemberId(team);
        }
    }

    // 발신자 로고 이미지 조회 헬퍼 메서드
    public String getSenderLogoImagePath(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            return profile.getProfileImagePath();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            return team.getTeamLogoImagePath();
        }
    }

    // 수신자 로고 이미지 조회 헬퍼 메서드
    public String getReceiverLogoImagePath(final Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                return profile.getProfileImagePath();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode =
                        matching.getReceiverType() == ReceiverType.TEAM
                                ? matching.getReceiverTeamCode()
                                : teamMemberAnnouncementQueryAdapter
                                        .getTeamMemberAnnouncement(
                                                matching.getReceiverAnnouncementId())
                                        .getTeam()
                                        .getTeamCode();
                return teamQueryAdapter.findByTeamCode(teamCode).getTeamLogoImagePath();
            }
            default -> throw new IllegalStateException(
                    "Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 발신자의 포지션 또는 규모 정보
    public String getSenderPositionOrTeamSize(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());

            ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                final ProfilePosition profilePosition =
                        profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
                senderProfilePositionDetail =
                        profilePositionMapper.toProfilePositionDetail(profilePosition);
            }

            return senderProfilePositionDetail.getMajorPosition();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            TeamScaleItem teamScaleItem = null;
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
                final TeamScale teamScale =
                        teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
                teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }

            return teamScaleItem.getTeamScaleName();
        }
    }

    // 수신자의 포지션 또는 규모 정보
    public String getReceiverPositionOrTeamSize(final Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                final Profile profile =
                        profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());

                ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();

                if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                    final ProfilePosition profilePosition =
                            profilePositionQueryAdapter.findProfilePositionByProfileId(
                                    profile.getId());
                    senderProfilePositionDetail =
                            profilePositionMapper.toProfilePositionDetail(profilePosition);
                }

                return senderProfilePositionDetail.getMajorPosition();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode =
                        matching.getReceiverType() == ReceiverType.TEAM
                                ? matching.getReceiverTeamCode()
                                : teamMemberAnnouncementQueryAdapter
                                        .getTeamMemberAnnouncement(
                                                matching.getReceiverAnnouncementId())
                                        .getTeam()
                                        .getTeamCode();
                final Team team = teamQueryAdapter.findByTeamCode(teamCode);
                // 팀 규모 조회
                TeamScaleItem teamScaleItem = null;
                if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
                    final TeamScale teamScale =
                            teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
                    teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                }
                return teamScaleItem.getTeamScaleName();
            }
            default -> throw new IllegalStateException(
                    "Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 발신자의 지역 정보
    public String getSenderRegionDetail(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            // 팀 지역 조회
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
                final ProfileRegion profileRegion =
                        regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
            }
            return RegionUtil.buildRegionString(
                    regionDetail.getCityName(), regionDetail.getDivisionName());
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
                final TeamRegion teamRegion =
                        regionQueryAdapter.findTeamRegionByTeamId(team.getId());
                regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
            }
            return RegionUtil.buildRegionString(
                    regionDetail.getCityName(), regionDetail.getDivisionName());
        }
    }

    // 수신자의 지역 정보
    public String getReceiverRegionOrAnnouncementSkill(final Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                // 팀 지역 조회
                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
                    final ProfileRegion profileRegion =
                            regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                    regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                }

                return RegionUtil.buildRegionString(
                        regionDetail.getCityName(), regionDetail.getDivisionName());
            }
            case TEAM -> {
                String teamCode = matching.getReceiverTeamCode();
                final Team team = teamQueryAdapter.findByTeamCode(teamCode);

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
                    final TeamRegion teamRegion =
                            regionQueryAdapter.findTeamRegionByTeamId(team.getId());
                    regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                }
                return RegionUtil.buildRegionString(
                        regionDetail.getCityName(), regionDetail.getDivisionName());
            }
            case ANNOUNCEMENT -> {
                final TeamMemberAnnouncement teamMemberAnnouncement =
                        teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                                matching.getReceiverAnnouncementId());
                // 스킬 조회
                List<AnnouncementSkill> announcementSkills =
                        announcementSkillQueryAdapter.getAnnouncementSkills(
                                teamMemberAnnouncement.getId());
                List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName>
                        announcementSkillNames =
                                announcementSkillMapper.toAnnouncementSkillNames(
                                        announcementSkills);

                // 리스트가 비어있지 않다면 첫 번째 값을 반환
                if (!announcementSkillNames.isEmpty()) {
                    return announcementSkillNames.get(0).getAnnouncementSkillName();
                }

                // 리스트가 비어 있다면 null 반환 또는 예외 처리
                return null;
            }
            default -> throw new IllegalStateException(
                    "Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 발신자의 이메일 정보
    public String getSenderEmail(Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            return profile.getMember().getEmail();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            final Member member =
                    teamMemberQueryAdapter.findTeamOwnerByTeamCode(team.getTeamCode());
            return member.getEmail();
        }
    }

    // 수신자의 이메일 정보
    public String getReceiverEmail(final Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                return profile.getMember().getEmail();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode =
                        matching.getReceiverType() == ReceiverType.TEAM
                                ? matching.getReceiverTeamCode()
                                : teamMemberAnnouncementQueryAdapter
                                        .getTeamMemberAnnouncement(
                                                matching.getReceiverAnnouncementId())
                                        .getTeam()
                                        .getTeamCode();
                final Team team = teamQueryAdapter.findByTeamCode(teamCode);
                final Member member =
                        teamMemberQueryAdapter.findTeamOwnerByTeamCode(team.getTeamCode());
                return member.getEmail();
            }
            default -> throw new IllegalStateException(
                    "Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    public String getSenderPositionOrTeamSizeText(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            return "포지션";
        } else {
            return "규모";
        }
    }

    public String getReceiverPositionOrTeamSizeText(final Matching matching) {
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            return "포지션";
        } else {
            return "규모";
        }
    }

    public String getReceiverRegionOrAnnouncementSkillText(final Matching matching) {
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            return "지역";
        } else if (matching.getReceiverType() == ReceiverType.TEAM) {
            return "지역";
        } else if (matching.getReceiverType() == ReceiverType.ANNOUNCEMENT) {
            return "요구 스킬";
        }
        return null;
    }

    public String getReceiverMajorPosition(final Matching matching) {
        final TeamMemberAnnouncement targetTeamMemberAnnouncement =
                teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(
                        matching.getReceiverAnnouncementId());
        final AnnouncementPositionItem announcementPositionItem =
                announcementCommonAssembler.fetchAnnouncementPositionItem(
                        targetTeamMemberAnnouncement);
        return announcementPositionItem.getMajorPosition();
    }
}
