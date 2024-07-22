package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.activity.ActivityRegion;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.activity.ActivityMethod;
import liaison.linkit.team.domain.activity.ActivityMethodTag;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.activity.ActivityMethodRepository;
import liaison.linkit.team.domain.repository.activity.ActivityMethodTagRepository;
import liaison.linkit.team.domain.repository.activity.ActivityRegionRepository;
import liaison.linkit.team.domain.repository.activity.RegionRepository;
import liaison.linkit.team.dto.request.activity.ActivityCreateRequest;
import liaison.linkit.team.dto.response.activity.ActivityMethodResponse;
import liaison.linkit.team.dto.response.activity.ActivityRegionResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
// 활동 방식 및 활동 지역 다루는 서비스 계층
public class ActivityService {

    final TeamProfileRepository teamProfileRepository;

    final ActivityMethodRepository activityMethodRepository;
    final ActivityMethodTagRepository activityMethodTagRepository;

    final ActivityRegionRepository activityRegionRepository;
    final RegionRepository regionRepository;

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    public void validateActivityMethodByMember(final Long memberId) {
        if (!activityMethodRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_ACTIVITY_METHOD_BY_TEAM_PROFILE_ID);
        }
    }

    public void validateActivityRegionByMember(final Long memberId) {
        if (!activityRegionRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_ACTIVITY_REGION_BY_TEAM_PROFILE_ID);
        }
    }

    // 활동 방식 저장
    public void saveActivityMethod(
            final Long memberId,
            final ActivityCreateRequest activityCreateRequest
    ) {

        // 팀 프로필 가져오기
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 삭제 처리 먼저 진행
        if (activityMethodRepository.existsByTeamProfileId(teamProfile.getId())) {
            activityMethodRepository.deleteAllByTeamProfileId(teamProfile.getId());
            teamProfile.updateIsActivityMethod(false);
        }

        // 활동 방식 태그 이름 구현 로직
        final List<ActivityMethodTag> activityMethodTags = activityMethodTagRepository
                .findActivityMethodTagByActivityTagNames(activityCreateRequest.getActivityTagNames());

        // 활동 방식
        final List<ActivityMethod> activityMethods = activityMethodTags.stream()
                .map(activityMethodTag -> new ActivityMethod(null, teamProfile, activityMethodTag))
                .toList();

        // 저장 로직
        activityMethodRepository.saveAll(activityMethods);

        // 팀 소개서 활동 방식 true 변환
        teamProfile.updateIsActivityMethod(true);
    }

    // 활동 지역 저장
    public void saveActivityRegion(
            final Long memberId,
            final ActivityCreateRequest activityCreateRequest
    ) {
        // 팀 소개서 객체 조회
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 삭제 처리 먼저 진행
        if (activityRegionRepository.existsByTeamProfileId(teamProfile.getId())) {
            activityRegionRepository.deleteAllByTeamProfileId(teamProfile.getId());
            teamProfile.updateIsActivityRegion(false);
        }

        // 지역 정보 구현 로직
        final Region region = regionRepository.findRegionByCityNameAndDivisionName(activityCreateRequest.getCityName(), activityCreateRequest.getDivisionName());

        ActivityRegion activityRegion = new ActivityRegion(null, teamProfile, region);

        // 활동 지역 저장
        activityRegionRepository.save(activityRegion);
        teamProfile.updateIsActivityRegion(true);
        // isActivity -> true였다면 그대로 / false였다면 더하기
        teamProfile.updateIsActivity();
        teamProfile.updateMemberTeamProfileTypeByCompletion();
    }

    // 활동 방식 전체 조회
    @Transactional(readOnly = true)
    public ActivityMethodResponse getAllActivityMethods(
            final Long memberId
    ) {
        log.info("getAllActivityMethods() 실행 여부");
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 저장되어 있는 활동 방식 리포지토리에서 모든 활동 방식 조회
        List<ActivityMethod> activityMethods = activityMethodRepository.findAllByTeamProfileId(teamProfile.getId());

        log.info("오류 찾기 part 1");

        List<String> activityTagNames = activityMethods.stream()
                .map(activityMethod -> activityMethodTagRepository.findById(activityMethod.getActivityMethodTag().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ActivityMethodTag::getActivityTagName)
                .toList();

        log.info("오류 찾기 part 2");
        return ActivityMethodResponse.of(activityTagNames);
    }

    @Transactional(readOnly = true)
    public ActivityRegionResponse getActivityRegion(final Long memberId) {
        log.info("getActivityRegion() 실행 여부");
        final TeamProfile teamProfile = getTeamProfile(memberId);

        ActivityRegion activityRegion = activityRegionRepository.findByTeamProfileId(teamProfile.getId())
                .orElseThrow(()-> new BadRequestException(NOT_FOUND_ACTIVITY_REGION_BY_TEAM_PROFILE_ID));

        return new ActivityRegionResponse(activityRegion.getRegion().getCityName(), activityRegion.getRegion().getDivisionName());
    }

    public ActivityResponse getActivity(final Long memberId) {
        log.info("getActivity() 메서드 실행 여부");
        return new ActivityResponse(getAllActivityMethods(memberId), getActivityRegion(memberId));
    }
}
