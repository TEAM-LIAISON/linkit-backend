package liaison.linkit.profile.service;

import java.util.List;
import liaison.linkit.profile.business.ProfileLogMapper;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileLogService {

    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileLogCommandAdapter profileLogCommandAdapter;
    private final ProfileLogMapper profileLogMapper;

    @Transactional(readOnly = true)
    public ProfileLogItems getProfileLogItems(final Long memberId) {
        log.info("memberId = {}의 내 로그 Items 조회 요청 발생했습니다.", memberId);

        final List<ProfileLog> profileLogs = profileLogQueryAdapter.getProfileLogs(memberId);
        log.info("profileLogs = {}가 성공적으로 조회되었습니다.", profileLogs);

        return profileLogMapper.toProfileLogItems(profileLogs);
    }

    @Transactional(readOnly = true)
    public ProfileLogItem getProfileLogItem(final Long memberId, final Long profileLogId) {
        log.info("memberId = {}의 내 로그 DTO 조회 요청 발생했습니다.", memberId);

        final ProfileLog profileLog = profileLogQueryAdapter.getProfileLog(profileLogId);
        log.info("profileLog = {}가 성공적으로 조회되었습니다.", profileLog);

        return profileLogMapper.toProfileLogItem(profileLog);
    }
}
