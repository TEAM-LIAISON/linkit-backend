package liaison.linkit.global.business.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import liaison.linkit.global.presentation.dto.HomeResponseDTO;
import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.member.presentation.dto.MemberDynamicResponse;
import liaison.linkit.profile.business.assembler.ProfileInformMenuAssembler;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogDynamicResponse;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.scrap.domain.repository.announcementScrap.AnnouncementScrapRepository;
import liaison.linkit.scrap.domain.repository.profileScrap.ProfileScrapRepository;
import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import liaison.linkit.search.presentation.dto.LogResponseDTO;
import liaison.linkit.search.presentation.dto.announcement.FlatAnnouncementDTO;
import liaison.linkit.search.presentation.dto.profile.FlatProfileDTO;
import liaison.linkit.search.presentation.dto.team.FlatTeamDTO;
import liaison.linkit.team.business.assembler.announcement.AnnouncementInformMenuAssembler;
import liaison.linkit.team.business.assembler.team.TeamInformMenuAssembler;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.log.TeamLogRepository;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.implement.log.TeamLogQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.AnnouncementDynamicResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogDynamicResponse;
import liaison.linkit.team.presentation.team.dto.TeamDynamicResponse;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.visit.dailyviewcount.domain.LogDailyViewCount;
import liaison.linkit.visit.dailyviewcount.domain.LogViewType;
import liaison.linkit.visit.dailyviewcount.repository.LogDailyViewCountRepository;
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
    private final ProfileRepository profileRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final ProfileLogRepository profileLogRepository;
    private final TeamLogRepository teamLogRepository;
    private final AnnouncementScrapRepository announcementScrapRepository;
    private final TeamScrapRepository teamScrapRepository;
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;
    private final TeamInformMenuAssembler teamInformMenuAssembler;
    private final ProfileInformMenuAssembler profileInformMenuAssembler;
    private final LogDailyViewCountRepository logDailyViewCountRepository;
    private final ProfileScrapRepository profileScrapRepository;
    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final TeamLogQueryAdapter teamLogQueryAdapter;

    public HomeResponseDTO.HomeResponse getHome() {
        List<FlatAnnouncementDTO> flatAnnouncementDTOS =
                teamMemberAnnouncementRepository.findHomeTopAnnouncements(9);
        List<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu> homeAnnouncementInformMenus =
                getAnnouncementInformMenus(9, Optional.empty(), flatAnnouncementDTOS);

        List<FlatTeamDTO> flatTeamDTOS = teamRepository.findHomeTopTeams(4);
        List<TeamResponseDTO.TeamInformMenu> homeTeamInformMenus =
                getTeamInformMenus(4, Optional.empty(), flatTeamDTOS);

        List<FlatProfileDTO> flatProfileDTOS = profileRepository.findHomeTopProfiles(6);
        List<ProfileResponseDTO.ProfileInformMenu> homeProfileInformMenus =
                getProfileInformMenus(6, Optional.empty(), flatProfileDTOS);

        List<LogDailyViewCount> topViews =
                logDailyViewCountRepository.findTop2ByLogViewTypeAndDateOrderByDailyViewCountDesc(
                        LocalDate.now());

        List<LogResponseDTO.LogInformMenu> result = new ArrayList<>();

        for (LogDailyViewCount viewCount : topViews) {
            Long logId = viewCount.getLogId();
            LogViewType type = viewCount.getLogViewType();

            if (type == LogViewType.PROFILE_LOG) {
                result.add(mapProfileLogToMenu(profileLogQueryAdapter.getProfileLog(logId)));
            } else if (type == LogViewType.TEAM_LOG) {
                result.add(mapTeamLogToMenu(teamLogQueryAdapter.getTeamLog(logId)));
            }
        }

        // 빈 배열일 경우, 랜덤으로 각 1개씩 조회
        if (result.isEmpty()) {
            ProfileLog randomProfileLog = profileLogQueryAdapter.getRandomProfileLog();
            TeamLog randomTeamLog = teamLogQueryAdapter.getRandomTeamLog();

            if (randomProfileLog != null) {
                result.add(mapProfileLogToMenu(randomProfileLog));
            }
            if (randomTeamLog != null) {
                result.add(mapTeamLogToMenu(randomTeamLog));
            }
        }

        return HomeResponseDTO.HomeResponse.builder()
                .announcementInformMenus(homeAnnouncementInformMenus)
                .teamInformMenus(homeTeamInformMenus)
                .profileInformMenus(homeProfileInformMenus)
                .logInformMenus(result)
                .build();
    }

    public LinkitDynamicResponseDTO.MemberDynamicListResponse getLinkitProfiles() {
        List<MemberDynamicResponse> memberDynamicResponses =
                memberRepository.findAllDynamicVariablesWithMember();
        return LinkitDynamicResponseDTO.MemberDynamicListResponse.of(memberDynamicResponses);
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

    @NotNull
    private List<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu>
            getAnnouncementInformMenus(
                    int limit, Optional<Long> optionalMemberId, List<FlatAnnouncementDTO> raw) {

        List<Long> announcementIds =
                raw.stream()
                        .map(FlatAnnouncementDTO::getTeamMemberAnnouncementId)
                        .distinct()
                        .toList();
        Set<Long> scraps =
                optionalMemberId
                        .map(
                                memberId ->
                                        announcementScrapRepository
                                                .findScrappedAnnouncementIdsByMember(
                                                        memberId, announcementIds))
                        .orElse(Set.of());

        Map<Long, Integer> scrapCounts =
                announcementScrapRepository.countScrapsGroupedByAnnouncement(announcementIds);

        List<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu> menus =
                announcementInformMenuAssembler.assembleAnnouncementInformMenus(
                        raw, scraps, scrapCounts);

        return menus.size() > limit ? menus.subList(0, limit) : menus;
    }

    @NotNull
    private List<TeamResponseDTO.TeamInformMenu> getTeamInformMenus(
            int size, Optional<Long> optionalMemberId, List<FlatTeamDTO> raw) {
        List<Long> teamIds = raw.stream().map(FlatTeamDTO::getTeamId).distinct().toList();
        Set<Long> scraps =
                optionalMemberId
                        .map(
                                memberId ->
                                        teamScrapRepository.findScrappedTeamIdsByMember(
                                                memberId, teamIds))
                        .orElse(Set.of());

        Map<Long, Integer> scrapCounts = teamScrapRepository.countScrapsGroupedByTeam(teamIds);

        List<TeamResponseDTO.TeamInformMenu> menus =
                teamInformMenuAssembler.assembleTeamInformMenus(raw, scraps, scrapCounts);

        return menus.size() > size ? menus.subList(0, size) : menus;
    }

    @NotNull
    private List<ProfileResponseDTO.ProfileInformMenu> getProfileInformMenus(
            int size, Optional<Long> optionalMemberId, List<FlatProfileDTO> raw) {
        List<Long> profileIds = raw.stream().map(FlatProfileDTO::getProfileId).distinct().toList();

        Set<Long> scraps =
                optionalMemberId
                        .map(
                                memberId ->
                                        profileScrapRepository.findScrappedProfileIdsByMember(
                                                memberId, profileIds))
                        .orElse(Set.of());

        Map<Long, Integer> scrapCounts =
                profileScrapRepository.countScrapsGroupedByProfile(profileIds);

        Map<Long, List<ProfileResponseDTO.ProfileTeamInform>> teamMap = Map.of();

        List<ProfileResponseDTO.ProfileInformMenu> menus =
                profileInformMenuAssembler.assembleProfileInformMenus(
                        raw, scraps, scrapCounts, teamMap);

        return menus.size() > size ? menus.subList(0, size) : menus;
    }

    // == ProfileLog -> LogInformMenu 변환 ==
    private LogResponseDTO.LogInformMenu mapProfileLogToMenu(ProfileLog pl) {
        return LogResponseDTO.LogInformMenu.builder()
                .id(pl.getId() != null ? pl.getId() : 0L)
                .domainType("PROFILE")
                .logTitle(pl.getLogTitle() != null ? pl.getLogTitle() : "")
                .logContent(pl.getLogContent() != null ? pl.getLogContent() : "")
                .createdAt(pl.getCreatedAt() != null ? pl.getCreatedAt() : LocalDateTime.now())
                .logInformDetails(
                        LogResponseDTO.LogInformDetails.profileLogType(
                                pl.getProfile() != null && pl.getProfile().getMember() != null
                                        ? pl.getProfile().getMember().getEmailId()
                                        : "",
                                pl.getId() != null ? pl.getId() : 0L,
                                pl.getProfile() != null
                                                && pl.getProfile().getMember() != null
                                                && pl.getProfile()
                                                                .getMember()
                                                                .getMemberBasicInform()
                                                        != null
                                        ? pl.getProfile()
                                                .getMember()
                                                .getMemberBasicInform()
                                                .getMemberName()
                                        : "",
                                pl.getProfile() != null
                                        ? pl.getProfile().getProfileImagePath()
                                        : ""))
                .build();
    }

    // == TeamLog -> LogInformMenu 변환 ==
    private LogResponseDTO.LogInformMenu mapTeamLogToMenu(TeamLog tl) {
        return LogResponseDTO.LogInformMenu.builder()
                .id(tl.getId() != null ? tl.getId() : 0L)
                .domainType("TEAM")
                .logTitle(tl.getLogTitle() != null ? tl.getLogTitle() : "")
                .logContent(tl.getLogContent() != null ? tl.getLogContent() : "")
                .createdAt(tl.getCreatedAt() != null ? tl.getCreatedAt() : LocalDateTime.now())
                .logInformDetails(
                        LogResponseDTO.LogInformDetails.teamLogType(
                                tl.getTeam() != null ? tl.getTeam().getTeamCode() : "",
                                tl.getId() != null ? tl.getId() : 0L,
                                tl.getTeam() != null ? tl.getTeam().getTeamName() : "",
                                tl.getTeam() != null ? tl.getTeam().getTeamLogoImagePath() : ""))
                .build();
    }
}
