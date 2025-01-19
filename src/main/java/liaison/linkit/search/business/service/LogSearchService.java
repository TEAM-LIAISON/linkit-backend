package liaison.linkit.search.business.service;

import liaison.linkit.profile.business.service.ProfileLogService;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.search.business.mapper.LogMapper;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenus;
import liaison.linkit.team.business.service.log.TeamLogService;
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

    private final ProfileLogService profileLogService;
    private final TeamLogService teamLogService;

    private final LogMapper logMapper;

    public LogInformMenus getHomeLogInformMenus() {

        // 1) ProfileLog 전부(or 조건) 조회

        return logMapper.toLogInformMenus();
    }
}
