package liaison.linkit.search.business.service;

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

    public LogInformMenus getHomeLogInformMenus() {

        // 1) ProfileLog 전부(or 조건) 조회
        List<ProfileLog> profileLogs = profileLogQueryAdapter.findTopView(2);
        // 2) TeamLog 전부(or 조건) 조회
        List<TeamLog> teamLogs = teamLogQueryAdapter.findTopView(2);

        // 3) 두 엔티티를 하나의 리스트로 합침 (최대 4개)
        List<Object> combined = new ArrayList<>();
        combined.addAll(profileLogs);
        combined.addAll(teamLogs);

        // 4) combined를 viewCount로 내림차순 정렬
        //    (ProfileLog, TeamLog에 getViewCount()가 있다고 가정)
        combined.sort(
                (a, b) -> {
                    long av =
                            (a instanceof ProfileLog)
                                    ? ((ProfileLog) a).getViewCount()
                                    : ((TeamLog) a).getViewCount();
                    long bv =
                            (b instanceof ProfileLog)
                                    ? ((ProfileLog) b).getViewCount()
                                    : ((TeamLog) b).getViewCount();
                    return Long.compare(bv, av); // 내림차순
                });

        // 5) 상위 2개만 추출
        List<Object> top4 = combined.stream().limit(2).toList();

        // 6) 각 엔티티를 LogInformMenu DTO로 변환
        List<LogInformMenu> mergedDTOs =
                top4.stream()
                        .map(
                                obj -> {
                                    if (obj instanceof ProfileLog pl) {
                                        return mapProfileLogToMenu(pl);
                                    } else {
                                        TeamLog tl = (TeamLog) obj;
                                        return mapTeamLogToMenu(tl);
                                    }
                                })
                        .toList();

        // 7) 필요한 형태로 감싸서 반환 (LogInformMenus)
        return logMapper.toLogInformMenus(mergedDTOs);
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
