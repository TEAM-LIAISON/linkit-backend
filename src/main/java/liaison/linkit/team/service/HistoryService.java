package liaison.linkit.team.service;

import static liaison.linkit.global.exception.ExceptionCode.HAVE_TO_INPUT_HISTORY;
import static liaison.linkit.global.exception.ExceptionCode.INVALID_HISTORY_WITH_TEAM_PROFILE;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_HISTORY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID;

import java.util.List;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.history.History;
import liaison.linkit.team.domain.repository.history.HistoryRepository;
import liaison.linkit.team.dto.request.HistoryCreateRequest;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HistoryService {

    private final HistoryRepository historyRepository;
    
    private History getHistory(final Long historyId) {
        return historyRepository.findById(historyId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_HISTORY_ID));
    }

    public void validateHistoryByMember(final Long memberId) {
        if (!historyRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(INVALID_HISTORY_WITH_TEAM_PROFILE);
        }
    }

    @Transactional(readOnly = true)
    public List<HistoryResponse> getAllHistories(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final List<History> histories = historyRepository.findAllByTeamProfileId(teamProfile.getId());
        return histories.stream()
                .map(this::getHistoryResponse)
                .toList();
    }

    private HistoryResponse getHistoryResponse(final History history) {
        return HistoryResponse.of(history);
    }

    public Long saveHistory(
            final Long memberId,
            final HistoryCreateRequest historyCreateRequest
    ) {
        if (historyCreateRequest.getHistoryOneLineIntroduction().isEmpty() ||
                historyCreateRequest.getHistoryIntroduction().isEmpty()
        ) {
            throw new BadRequestException(HAVE_TO_INPUT_HISTORY);
        }

        final TeamProfile teamProfile = getTeamProfile(memberId);

        if (teamProfile.getIsHistory()) {
            return saveHistoryMethod(teamProfile, historyCreateRequest);
        } else {
            teamProfile.updateIsHistory(true);
            return saveHistoryMethod(teamProfile, historyCreateRequest);
        }
    }

    public void saveHistories(
            final Long memberId,
            final List<HistoryCreateRequest> historyCreateRequests
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        if (historyRepository.existsByTeamProfileId(teamProfile.getId())) {
            historyRepository.deleteAllByTeamProfileId(teamProfile.getId());
            teamProfile.updateIsHistory(false);
        }

        historyCreateRequests.forEach(request -> {
            saveHistoryMethod(teamProfile, request);
        });

        teamProfile.updateIsHistory(true);
    }

    private Long saveHistoryMethod(
            final TeamProfile teamProfile,
            final HistoryCreateRequest historyCreateRequest
    ) {
        final History newHistory = History.of(
                teamProfile,
                historyCreateRequest.getHistoryOneLineIntroduction(),
                historyCreateRequest.getStartYear(),
                historyCreateRequest.getEndYear(),
                historyCreateRequest.isInProgress(),
                historyCreateRequest.getHistoryIntroduction()
        );

        return historyRepository.save(newHistory).getId();
    }

    public void deleteHistory(final Long memberId, final Long historyId) {
        log.info("History 삭제 메서드 실행");
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final History history = getHistory(historyId);

        historyRepository.deleteById(history.getId());

        log.info("History 삭제 완료");
        if (!historyRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfile.cancelTeamPerfectionTwoPointFive();
        }
    }

    public Long updateHistory(Long historyId, HistoryCreateRequest historyCreateRequest) {
        final History history = getHistory(historyId);
        history.update(historyCreateRequest);
        return history.getId();
    }
}



