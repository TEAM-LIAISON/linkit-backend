package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.history.History;
import liaison.linkit.team.domain.repository.HistoryRepository;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.dto.request.HistoryCreateRequest;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HistoryService {

    private final TeamProfileRepository teamProfileRepository;
    private final HistoryRepository historyRepository;

    // 모든 "팀 소개서" 서비스 계층에 필요한 TeamProfile 조회 메서드
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

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

    public void saveHistories(
            final Long memberId,
            final List<HistoryCreateRequest> historyCreateRequests
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        if (historyRepository.existsByTeamProfileId(teamProfile.getId())) {
            historyRepository.deleteAllByTeamProfileId(teamProfile.getId());
            teamProfile.updateIsHistory(false);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }

        historyCreateRequests.forEach(request -> {
            saveHistory(teamProfile, request);
        });

        teamProfile.updateIsHistory(true);
        teamProfile.updateMemberTeamProfileTypeByCompletion();
    }

    private void saveHistory(
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

        historyRepository.save(newHistory);
    }

    public void deleteHistory(final Long memberId, final Long historyId) {
        log.info("History 삭제 메서드 실행");
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final History history = getHistory(historyId);

        historyRepository.deleteById(history.getId());

        log.info("History 삭제 완료");
        if (!historyRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfile.cancelTeamPerfectionTwoPointFive();
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }
    }




//    public void update(final Long memberId, final HistoryUpdateRequest historyUpdateRequest) {
//        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);
//        final Long historyId = validateHistoryByMember(memberId);
//
//        final History history = historyRepository.findById(historyId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_HISTORY_ID));
//
//        history.update(historyUpdateRequest);
//        historyRepository.save(history);
//    }

}



