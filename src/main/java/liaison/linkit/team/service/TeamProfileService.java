package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.dto.request.TeamIntroductionCreateRequest;
import liaison.linkit.team.dto.response.OnBoardingTeamProfileResponse;
import liaison.linkit.team.dto.response.TeamProfileOnBoardingIsValueResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFieldTeamInformResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamProfileService {

    private final TeamProfileRepository teamProfileRepository;

    // 회원에 대한 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
    private TeamProfile getTeamProfileByMember(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    // 멤버 ID -> 팀 소개서 유효성 검증 -> 유효하지 않다면 에러코드 반환
    public void validateTeamProfileByMember(final Long memberId) {
        if (!teamProfileRepository.existsByMemberId(memberId)) {
            throw new AuthException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID);
        }
    }

//    public OnBoardingFieldTeamInformResponse getOnBoardingFieldTeamInformResponse(
//            final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse,
//            final TeamMiniProfileEarlyOnBoardingResponse teamMiniProfileEarlyOnBoardingResponse
//    ) {
//        return new OnBoardingFieldTeamInformResponse(
//                teamProfileTeamBuildingFieldResponse.getTeamBuildingFieldNames(),
//                teamMiniProfileEarlyOnBoardingResponse.getTeamName(),
//                teamMiniProfileEarlyOnBoardingResponse.getSectorName()
//        )
//    }

    // 팀 소개서 온보딩 값 존재성 boolean 값들 전달
    public TeamProfileOnBoardingIsValueResponse getTeamProfileOnBoardingIsValue(final Long memberId) {
        return TeamProfileOnBoardingIsValueResponse.teamProfileOnBoardingIsValue(getTeamProfileByMember(memberId));
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


    public void saveTeamIntroduction(
            final Long memberId,
            final TeamIntroductionCreateRequest teamIntroductionCreateRequest
    ) {
        final TeamProfile teamProfile = getTeamProfileByMember(memberId);
        teamProfile.updateTeamIntroduction(teamIntroductionCreateRequest.getTeamIntroduction());
    }
}
