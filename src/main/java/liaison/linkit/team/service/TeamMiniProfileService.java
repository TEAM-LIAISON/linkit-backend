package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import liaison.linkit.image.domain.S3ImageEvent;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.miniprofile.IndustrySector;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.miniprofile.TeamScale;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.IndustrySectorRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamScaleRepository;
import liaison.linkit.team.dto.request.miniprofile.TeamMiniProfileCreateRequest;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMiniProfileService {


    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final IndustrySectorRepository industrySectorRepository;
    private final TeamScaleRepository teamScaleRepository;

    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
    }

    private TeamMiniProfile getTeamMiniProfile(final Long teamProfileId) {
        return teamMiniProfileRepository.findByTeamProfileId(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
    }

    // 팀 소개서 유효성 판단
    public void validateTeamMiniProfileByMember(final Long memberId) {
        if (!teamMiniProfileRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID);
        }
    }

    public TeamMiniProfileEarlyOnBoardingResponse getTeamMiniProfileEarlyOnBoarding(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findByTeamProfileId(teamProfile.getId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
        return TeamMiniProfileEarlyOnBoardingResponse.personalTeamMiniProfileOnBoarding(teamMiniProfile);
    }

    public void saveOnBoarding(
            final Long memberId,
            final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest
    ) {
        // 팀 프로필 객체 조회
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 팀 미니 프로필 객체가 존재했다면? 삭제 먼저?
        //
        if (teamMiniProfileRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamMiniProfileRepository.deleteByTeamProfileId(teamProfile.getId());
        }

        // IndustrySector 찾기
        final IndustrySector industrySector = industrySectorRepository.findBySectorName(onBoardingFieldTeamInformRequest.getSectorName());

        // TeamScale 찾기
        final TeamScale teamScale = teamScaleRepository.findBySizeType(onBoardingFieldTeamInformRequest.getSizeType());


        final TeamMiniProfile teamMiniProfile = TeamMiniProfile.of(
                teamProfile,
                industrySector,
                teamScale,
                onBoardingFieldTeamInformRequest.getTeamName(),
                null,
                null,
                null
        );

        teamMiniProfileRepository.save(teamMiniProfile);
        teamProfile.updateIsTeamMiniProfile(true);
    }


    // 기존에 온보딩 기본 정보 (팀 이름, 규모, 분야)를 입력해야 실행 가능함.
    public void updateTeamMiniProfile(
            final Long memberId,
            final TeamMiniProfileCreateRequest teamMiniProfileCreateRequest,
            final MultipartFile teamMiniProfileImage
    ) {
        // 미니 프로필 내에 기본 정보를 입력한 사람과 입력하지 않은 사람으로 나뉜다.
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 팀 미니 프로필이 존재하는지 판단
        if (!getIsTeamMiniProfile(memberId)) {
            throw new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_MEMBER_ID);
        }

        // 존재하는 경우 -> 2가지 존재
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfile(teamProfile.getId());

        // 1. 온보딩 기본 정보 입력한 사람 2. 다른 정보까지 입력했었던 사람

        // 1. 해당
        if (teamMiniProfile.getTeamName() != null && teamMiniProfile.getTeamProfileTitle() == null) {
            // 업로드되었었던 이미지가 없었을 것
            final List<TeamMiniProfileKeyword> teamMiniProfileKeywordList = teamMiniProfileCreateRequest.getTeamKeywordNames().stream()
                    .map(keyWordName -> new TeamMiniProfileKeyword(null, teamMiniProfile, keyWordName))
                    .toList();
            if (teamMiniProfileImage != null) { // case 1.1.
                final String teamMiniProfileImageUrl = saveTeamMiniProfileImage(teamMiniProfileImage);

                teamMiniProfile.onBoardingTeamMiniProfile(
                        teamMiniProfileCreateRequest.getTeamProfileTitle(),
                        teamMiniProfileCreateRequest.getIsTeamActivate(),
                        teamMiniProfileImageUrl
                );

                teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
            } else {                            // case 1.2.
                teamMiniProfile.onBoardingTeamMiniProfile(
                        teamMiniProfileCreateRequest.getTeamProfileTitle(),
                        teamMiniProfileCreateRequest.getIsTeamActivate(),
                        null
                );
                teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
            }
        } else {
            // case 2.1. 다른 정보까지 입력했었는데, 이미지도 업로드했던 사람
            if (teamMiniProfile.getTeamLogoImageUrl() != null) {
                final List<TeamMiniProfileKeyword> teamMiniProfileKeywordList = teamMiniProfileCreateRequest.getTeamKeywordNames().stream()
                        .map(keyWordName -> new TeamMiniProfileKeyword(null, teamMiniProfile, keyWordName))
                        .toList();
                // case 2.1.1.
                if (teamMiniProfileImage != null) {
                    // S3 이미지 삭제
                    s3Uploader.deleteImage(teamMiniProfile.getTeamLogoImageUrl());

                    // 새로운 이미지를 S3에 저장
                    final String teamMiniProfileImageUrl = saveTeamMiniProfileImage(teamMiniProfileImage);

                    // 객체 업데이트
                    teamMiniProfile.onBoardingTeamMiniProfile(
                            teamMiniProfileCreateRequest.getTeamProfileTitle(),
                            teamMiniProfileCreateRequest.getIsTeamActivate(),
                            teamMiniProfileImageUrl
                    );

                    // 새롭게 전달 받은 키워드 리스트 저장
                    teamMiniProfileKeywordRepository.deleteAllByTeamMiniProfileId(teamMiniProfile.getId());
                    teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
                } else {    // case 2.1.2.
                    // 기존 이미지 그대로 사용하는 것으로 간주
                    teamMiniProfile.onBoardingTeamMiniProfile(
                            teamMiniProfileCreateRequest.getTeamProfileTitle(),
                            teamMiniProfileCreateRequest.getIsTeamActivate(),
                            teamMiniProfile.getTeamLogoImageUrl()
                    );

                    teamMiniProfileKeywordRepository.deleteAllByTeamMiniProfileId(teamMiniProfile.getId());
                    teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
                }
            } else { // case 2.2 다른 정보까지 입력했었는데, 이미지는 업로드하지 않았던 사람
                final List<TeamMiniProfileKeyword> teamMiniProfileKeywordList = teamMiniProfileCreateRequest.getTeamKeywordNames().stream()
                        .map(keyWordName -> new TeamMiniProfileKeyword(null, teamMiniProfile, keyWordName))
                        .toList();

                // 이미지 요청이 이번에는 발생
                // case 2.2.1.
                if (teamMiniProfileImage != null) {
                    // 새로운 이미지를 S3에 저장
                    final String teamMiniProfileImageUrl = saveTeamMiniProfileImage(teamMiniProfileImage);

                    // 객체 업데이트
                    teamMiniProfile.onBoardingTeamMiniProfile(
                            teamMiniProfileCreateRequest.getTeamProfileTitle(),
                            teamMiniProfileCreateRequest.getIsTeamActivate(),
                            teamMiniProfileImageUrl
                    );

                    teamMiniProfileKeywordRepository.deleteAllByTeamMiniProfileId(teamMiniProfile.getId());
                    teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
                } else { // case 2.2.2.
                    // 객체 업데이트
                    teamMiniProfile.onBoardingTeamMiniProfile(
                            teamMiniProfileCreateRequest.getTeamProfileTitle(),
                            teamMiniProfileCreateRequest.getIsTeamActivate(),
                            null
                    );
                    teamMiniProfileKeywordRepository.deleteAllByTeamMiniProfileId(teamMiniProfile.getId());
                    teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
                }
            }
        }

        // 1. 팀 미니 프로필 이미지가 존재했던 경우
        if (teamMiniProfile.getTeamLogoImageUrl() != null) {
            // 리스트 객체 변환
            final List<TeamMiniProfileKeyword> teamMiniProfileKeywordList = teamMiniProfileCreateRequest.getTeamKeywordNames().stream()
                    .map(keyWordName -> new TeamMiniProfileKeyword(null, teamMiniProfile, keyWordName))
                    .toList();

            // 팀 미니 프로필 이미지 객체가 실제로 들어온 경우 -> 이미지 수정으로 간주 가능
            if (teamMiniProfileImage != null) {
                // S3 이미지 삭제
                s3Uploader.deleteImage(teamMiniProfile.getTeamLogoImageUrl());

                // 새로운 이미지를 S3에 저장
                final String teamMiniProfileImageUrl = saveTeamMiniProfileImage(teamMiniProfileImage);

                // 객체 업데이트
                teamMiniProfile.onBoardingTeamMiniProfile(
                        teamMiniProfileCreateRequest.getTeamProfileTitle(),
                        teamMiniProfileCreateRequest.getIsTeamActivate(),
                        teamMiniProfileImageUrl
                );

                // 새롭게 전달 받은 키워드 리스트 저장
                teamMiniProfileKeywordRepository.deleteAllByTeamMiniProfileId(teamMiniProfile.getId());
                teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
            } else {

                // 기존 이미지 그대로 사용하는 것으로 간주
                teamMiniProfile.onBoardingTeamMiniProfile(
                        teamMiniProfileCreateRequest.getTeamProfileTitle(),
                        teamMiniProfileCreateRequest.getIsTeamActivate(),
                        teamMiniProfile.getTeamLogoImageUrl()
                );

                teamMiniProfileKeywordRepository.deleteAllByTeamMiniProfileId(teamMiniProfile.getId());
                teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
            }
        } else {
            // 2. 팀 미니 프로필 이미지가 없었던 경우 -> 기존에 이미지를 업로드하지 않은 사람과 새롭게 등록하는 사람
            final List<TeamMiniProfileKeyword> teamMiniProfileKeywordList = teamMiniProfileCreateRequest.getTeamKeywordNames().stream()
                    .map(keyWordName -> new TeamMiniProfileKeyword(null, teamMiniProfile, keyWordName))
                    .toList();

            // 이미지가 새롭게 요청이 옴
            if (teamMiniProfileImage != null) {
                // 새로운 이미지를 S3에 저장
                final String teamMiniProfileImageUrl = saveTeamMiniProfileImage(teamMiniProfileImage);

                // 객체 업데이트
                teamMiniProfile.onBoardingTeamMiniProfile(
                        teamMiniProfileCreateRequest.getTeamProfileTitle(),
                        teamMiniProfileCreateRequest.getIsTeamActivate(),
                        teamMiniProfileImageUrl
                );

                // 새롭게 전달 받은 키워드 리스트 저장
                teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
            } else {
                // 기존 이미지 그대로 사용하는 것으로 간주
                teamMiniProfile.onBoardingTeamMiniProfile(
                        teamMiniProfileCreateRequest.getTeamProfileTitle(),
                        teamMiniProfileCreateRequest.getIsTeamActivate(),
                        teamMiniProfile.getTeamLogoImageUrl()
                );
                // 기존에 저장되어있던 keywordList 전체 삭제
                teamMiniProfileKeywordRepository.deleteAllByTeamMiniProfileId(teamMiniProfile.getId());
                teamMiniProfileKeywordRepository.saveAll(teamMiniProfileKeywordList);
            }
        }
    }

    // 온보딩 안하고 미니 프로필 생성하려는 사람들 (아직 디자인 안나옴 06_23 기준)
    public void saveNewTeamMiniProfile(
            final Long memberId,
            final TeamMiniProfileCreateRequest teamMiniProfileCreateRequest,
            final MultipartFile teamMiniProfileImage
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

    }

    private String saveTeamMiniProfileImage(final MultipartFile teamMiniProfileImage) {
        validateSizeofImage(teamMiniProfileImage);
        final ImageFile imageFile = new ImageFile(teamMiniProfileImage);
        return uploadTeamMiniProfileImage(imageFile);
    }

    private String uploadTeamMiniProfileImage(
            final ImageFile teamMiniProfileImage
    ) {
        try {
            return s3Uploader.uploadMiniProfileImage(teamMiniProfileImage);
        } catch (final ImageException e) {
            publisher.publishEvent(new S3ImageEvent(teamMiniProfileImage.getHashedName()));
            throw e;
        }
    }


    private void validateSizeofImage(
            final MultipartFile teamMiniProfileImage
    ) {
        if (teamMiniProfileImage == null || teamMiniProfileImage.isEmpty()) {
            throw new ImageException(EMPTY_IMAGE);
        }
    }

    public TeamMiniProfileResponse getPersonalTeamMiniProfile(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfile(teamProfile.getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());
        return TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword);
    }

    public boolean getIsTeamMiniProfile(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        return teamProfile.getIsTeamMiniProfile();
    }

    public String getTeamName(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfile(teamProfile.getId());
        return teamMiniProfile.getTeamName();
    }

    public void updateOnBoarding(
            final Long memberId,
            final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        final TeamMiniProfile teamMiniProfile = getTeamMiniProfile(teamProfile.getId());
        // IndustrySector 찾기
        final IndustrySector industrySector = industrySectorRepository.findBySectorName(onBoardingFieldTeamInformRequest.getSectorName());
        // TeamScale 찾기
        final TeamScale teamScale = teamScaleRepository.findBySizeType(onBoardingFieldTeamInformRequest.getSizeType());

        teamMiniProfile.updateOnBoarding(
                onBoardingFieldTeamInformRequest.getTeamName(),
                industrySector,
                teamScale
        );
        
        log.info("memberId={}의 팀 미니 프로필 온보딩 항목 수정이 완료되었습니다.", memberId);
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
