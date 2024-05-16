package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.History;
import liaison.linkit.team.domain.repository.HistoryRepository;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.dto.response.HistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_HISTORY_WITH_TEAM_PROFILE;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_HISTORY_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final TeamProfileRepository teamProfileRepository;

    public Long validateHistoryByMember(final Long memberId) {
        Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
        if (!historyRepository.existsByTeamProfileId(teamProfileId)) {
            throw new AuthException(INVALID_HISTORY_WITH_TEAM_PROFILE);
        } else {
            return historyRepository.findByTeamProfileId(teamProfileId).getId();
        }

    }

    @Transactional(readOnly = true)
    public HistoryResponse getHistoryDetail(final Long historyId) {
        final History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_HISTORY_ID));
        return HistoryResponse.personalHistory(history);
    }
}
