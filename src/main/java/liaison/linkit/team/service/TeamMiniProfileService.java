package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_MINI_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMiniProfileService {
    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    // 팀 소개서 유효성 판단
    public Long validateTeamMiniProfileByMember(final Long memberId) {
        Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
        if (!teamMiniProfileRepository.existsByTeamProfileId(teamProfileId)) {
            throw new AuthException(ExceptionCode.INVALID_TEAM_MINI_PROFILE_WITH_MEMBER);
        } else {
            return teamMiniProfileRepository.findByTeamProfileId(teamProfileId).getId();
        }
    }

    public TeamMiniProfileResponse getTeamMiniProfileDetail(final Long teamMiniProfileId) {
        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findById(teamMiniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_ID));
        return TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile);
    }

    public TeamMiniProfileEarlyOnBoardingResponse getTeamMiniProfileEarlyOnBoarding(final Long teamMiniProfileId) {
        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findById(teamMiniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_ID));
        return TeamMiniProfileEarlyOnBoardingResponse.personalTeamMiniProfileOnBoarding(teamMiniProfile);
    }


//    private final TeamProfileRepository teamProfileRepository;
//    private final TeamMiniProfileRepository teamMiniProfileRepository;
//
//    public Long validateTeamMiniProfileByMember(final Long memberId) {
//        Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
//        if (!teamMiniProfileRepository.existsByTeamProfileId(teamProfileId)) {
//            throw new AuthException(INVALID_TEAM_MINI_PROFILE_WITH_MEMBER);
//        } else {
//            return teamMiniProfileRepository.findByTeamProfileId(teamProfileId).getId();
//        }
//    }
//
//    public void save(
//            final Long memberId,
//            final TeamMiniProfileCreateRequest teamMiniProfileCreateRequest
//    ) {
//        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);
//
//        final TeamMiniProfile newTeamMiniProfile = TeamMiniProfile.of(
//
//
//                teamMiniProfileCreateRequest.getTeamName(),
//                teamMiniProfileCreateRequest.getTeamOneLineIntroduction(),
//                teamMiniProfileCreateRequest.getTeamLink()
//        );
//
//    }
//
//    @Transactional(readOnly = true)
//    public TeamMiniProfileResponse getTeamMiniProfileDetail(final Long teamMiniProfileId) {
//        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findById(teamMiniProfileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_ID));
//        return TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile);
//    }


}
