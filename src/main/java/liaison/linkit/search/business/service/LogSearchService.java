package liaison.linkit.search.business.service;

import liaison.linkit.profile.business.service.ProfileLogService;
import liaison.linkit.search.business.mapper.LogMapper;
import liaison.linkit.team.business.service.log.TeamLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LogSearchService {

    private final ProfileLogService profileLogService;
    private final TeamLogService teamLogService;

    private final LogMapper logMapper;

}
