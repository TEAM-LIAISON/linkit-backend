package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.dto.request.TeamIntroductionCreateRequest;
import liaison.linkit.team.dto.response.*;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFieldTeamInformResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamProfileService {

    private final TeamProfileRepository teamProfileRepository;

    // 회원에 대한 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    // 멤버 ID -> 팀 소개서 유효성 검증 -> 유효하지 않다면 에러코드 반환
    public void validateTeamProfileByMember(final Long memberId) {
        if (!teamProfileRepository.existsByMemberId(memberId)) {
            throw new AuthException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID);
        }
    }

    // 팀 소개서 온보딩 값 존재성 boolean 값들 전달
    public TeamProfileOnBoardingIsValueResponse getTeamProfileOnBoardingIsValue(final Long memberId) {
        return TeamProfileOnBoardingIsValueResponse.teamProfileOnBoardingIsValue(getTeamProfile(memberId));
    }

    public OnBoardingTeamProfileResponse getOnBoardingTeamProfile(
            final OnBoardingFieldTeamInformResponse onBoardingFieldTeamInformResponse,
            final ActivityResponse activityResponse,
            final TeamMiniProfileResponse teamMiniProfileResponse
    ) {
        return OnBoardingTeamProfileResponse.onBoardingTeamProfileItems(
                onBoardingFieldTeamInformResponse,
                activityResponse,
                teamMiniProfileResponse
        );
    }

    // 팀 소개 저장 메서드
    public void saveTeamIntroduction(
            final Long memberId,
            final TeamIntroductionCreateRequest teamIntroductionCreateRequest
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        teamProfile.updateTeamIntroduction(teamIntroductionCreateRequest.getTeamIntroduction());
    }

    // 팀 프로필 기입 여부 값 전체 조회 메서드
    @Transactional(readOnly = true)
    public TeamProfileIsValueResponse getTeamProfileIsValue(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        return TeamProfileIsValueResponse.teamProfileIsValue(teamProfile);
    }

    @Transactional(readOnly = true)
    public TeamProfileIntroductionResponse getTeamIntroduction(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        return TeamProfileIntroductionResponse.teamProfileIntroduction(teamProfile);
    }

    public TeamProfileResponse getTeamProfileResponse(
            final boolean isTeamProfileEssential,
            final TeamMiniProfileResponse teamMiniProfileResponse,
            final TeamCompletionResponse teamCompletionResponse,
            final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse,
            final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponse,
            final ActivityResponse activityResponse,
            final TeamProfileIntroductionResponse teamProfileIntroductionResponse,
            final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponse,
            final List<HistoryResponse> historyResponse,
            final TeamAttachResponse teamAttachResponse
    ) {
        return TeamProfileResponse.teamProfileItems(
                isTeamProfileEssential,
                teamMiniProfileResponse,
                teamCompletionResponse,
                teamProfileTeamBuildingFieldResponse,
                teamMemberAnnouncementResponse,
                activityResponse,
                teamProfileIntroductionResponse,
                teamMemberIntroductionResponse,
                historyResponse,
                teamAttachResponse
        );
    }
}
