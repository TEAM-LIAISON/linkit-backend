package liaison.linkit.search.business.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.search.business.mapper.LogMapper;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformDetails;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenu;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenus;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.implement.log.TeamLogQueryAdapter;
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
public class LogSearchService {

    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final TeamLogQueryAdapter teamLogQueryAdapter;

    private final LogMapper logMapper;

    private final LogDailyViewCountRepository logDailyViewCountRepository;

    public LogInformMenus getHomeLogInformMenus() {

        List<LogDailyViewCount> topViews =
                logDailyViewCountRepository.findTop2ByLogViewTypeAndDateOrderByDailyViewCountDesc(
                        LocalDate.now());

        List<LogInformMenu> result = new ArrayList<>();

        for (LogDailyViewCount viewCount : topViews) {
            Long logId = viewCount.getLogId();
            LogViewType type = viewCount.getLogViewType();

            if (type == LogViewType.PROFILE_LOG) {
                result.add(mapProfileLogToMenu(profileLogQueryAdapter.getProfileLog(logId)));
            } else if (type == LogViewType.TEAM_LOG) {
                result.add(mapTeamLogToMenu(teamLogQueryAdapter.getTeamLog(logId)));
            }
        }

        return logMapper.toLogInformMenus(result);
    }

    // == ProfileLog -> LogInformMenu 변환 ==
    private LogInformMenu mapProfileLogToMenu(ProfileLog pl) {
        return LogInformMenu.builder()
                .id(pl.getId() != null ? pl.getId() : 0L)
                .domainType("PROFILE")
                .logTitle(pl.getLogTitle() != null ? pl.getLogTitle() : "")
                .logContent(pl.getLogContent() != null ? pl.getLogContent() : "")
                .createdAt(pl.getCreatedAt() != null ? pl.getCreatedAt() : LocalDateTime.now())
                .logInformDetails(
                        LogInformDetails.profileLogType(
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
    private LogInformMenu mapTeamLogToMenu(TeamLog tl) {
        return LogInformMenu.builder()
                .id(tl.getId() != null ? tl.getId() : 0L)
                .domainType("TEAM")
                .logTitle(tl.getLogTitle() != null ? tl.getLogTitle() : "")
                .logContent(tl.getLogContent() != null ? tl.getLogContent() : "")
                .createdAt(tl.getCreatedAt() != null ? tl.getCreatedAt() : LocalDateTime.now())
                .logInformDetails(
                        LogInformDetails.teamLogType(
                                tl.getTeam() != null ? tl.getTeam().getTeamCode() : "",
                                tl.getId() != null ? tl.getId() : 0L,
                                tl.getTeam() != null ? tl.getTeam().getTeamName() : "",
                                tl.getTeam() != null ? tl.getTeam().getTeamLogoImagePath() : ""))
                .build();
    }
}
