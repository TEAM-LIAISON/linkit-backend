package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.FileException;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.attach.TeamAttachUrl;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import liaison.linkit.team.domain.repository.attach.teamAttachUrl.TeamAttachUrlRepository;
import liaison.linkit.team.dto.request.attach.TeamAttachUrlCreateRequest;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachUrlResponse;
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
public class TeamAttachService {
    private final TeamProfileRepository teamProfileRepository;
    private final TeamAttachUrlRepository teamAttachUrlRepository;
//    private final TeamAttachFileRepository teamAttachFileRepository;
    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    // 모든 "팀 소개서" 서비스 계층에 필요한 TeamProfile 조회 메서드
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

//    public void validateTeamAttachFileByMember(final Long memberId) {
//        if (!teamAttachFileRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
//            throw new AuthException(INVALID_TEAM_ATTACH_URL_WITH_PROFILE);
//        }
//    }

    public void validateTeamAttachUrlByMember(final Long memberId) {
        if (!teamAttachUrlRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(INVALID_TEAM_ATTACH_URL_WITH_PROFILE);
        }
    }

    private TeamAttachUrl getTeamAttachUrl(final Long teamAttachUrlId) {
        return teamAttachUrlRepository.findById(teamAttachUrlId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_ATTACH_URL_ID));
    }

    // 팀 첨부 URL 저장
    public void saveUrl(
            final Long memberId,
            final List<TeamAttachUrlCreateRequest> teamAttachUrlCreateRequests
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        // 존재 이력이 있으면 전체 삭제 먼저
        if (teamAttachUrlRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamAttachUrlRepository.deleteAllByTeamProfileId(teamProfile.getId());
            teamProfile.updateIsTeamAttachUrl(false);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }

        teamAttachUrlCreateRequests.forEach(request ->{
            saveTeamAttachUrl(teamProfile, request);
        });

        teamProfile.updateIsTeamAttachUrl(true);
        teamProfile.updateMemberTeamProfileTypeByCompletion();
    }

    private void saveTeamAttachUrl(final TeamProfile teamProfile, final TeamAttachUrlCreateRequest teamAttachUrlCreateRequest) {
        final TeamAttachUrl newTeamAttachUrl = TeamAttachUrl.of(
                teamProfile,
                teamAttachUrlCreateRequest.getTeamAttachUrlName(),
                teamAttachUrlCreateRequest.getTeamAttachUrlPath()
        );
        teamAttachUrlRepository.save(newTeamAttachUrl);
    }

    // 파일 저장 메서드 실행부
//    public void saveFile(
//            final Long memberId,
//            final MultipartFile teamAttachFile
//    ) {
//        final TeamProfile teamProfile = getTeamProfile(memberId);
//        final String teamAttachFileUrl = saveFileS3(teamAttachFile);
//
//        final TeamAttachFile newAttachFile = TeamAttachFile.of(
//                teamProfile,
//                teamAttachFile.getOriginalFilename(),
//                teamAttachFileUrl
//        );
//
//        teamAttachFileRepository.save(newAttachFile);
//        teamProfile.updateIsTeamAttachFile(true);
//        teamProfile.updateMemberTeamProfileTypeByCompletion();
//    }

    // S3에 파일 저장
//    private String saveFileS3(final MultipartFile teamAttachFile) {
//        validateSizeofFile(teamAttachFile);
//        final PortfolioFile portfolioFile = new PortfolioFile(teamAttachFile);
//        return uploadPortfolioFile(portfolioFile);
//    }

    // 포트폴리오 파일 업로드
//    private String uploadPortfolioFile(final PortfolioFile portfolioFile) {
//        try {
//            return s3Uploader.uploadPortfolioFile(portfolioFile);
//        } catch (final Exception e) {
//            publisher.publishEvent(new S3PortfolioEvent(portfolioFile.getHashedName()));
//            throw e;
//        }
//    }

    // 파일 유효성 판단
    private void validateSizeofFile(final MultipartFile teamAttachFile) {
        if (teamAttachFile == null || teamAttachFile.isEmpty()) {
            throw new FileException(EMPTY_TEAM_ATTACH_FILE);
        }
    }

    public TeamAttachResponse getTeamAttachList(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        final List<TeamAttachUrl> teamAttachUrls = teamAttachUrlRepository.findAllByTeamProfileId(teamProfile.getId());
        log.info("teamAttachUrls={}", teamAttachUrls);

//        final List<TeamAttachFile> teamAttachFiles = teamAttachFileRepository.findAllByTeamProfileId(teamProfile.getId());
//        log.info("teamAttachFiles={}", teamAttachFiles);

        final List<TeamAttachUrlResponse> teamAttachUrlResponses = teamAttachUrls.stream().map(this::getTeamAttachUrlResponse).toList();
//        final List<TeamAttachFileResponse> teamAttachFileResponses = teamAttachFiles.stream().map(this::getTeamAttachFileResponse).toList();

        return TeamAttachResponse.getTeamAttachResponse(teamAttachUrlResponses);
    }

//    private TeamAttachFileResponse getTeamAttachFileResponse(
//            final TeamAttachFile teamAttachFile
//    ) {
//        return TeamAttachFileResponse.getTeamAttachFile(teamAttachFile);
//    }

    private TeamAttachUrlResponse getTeamAttachUrlResponse(
            final TeamAttachUrl teamAttachUrl
    ) {
        return TeamAttachUrlResponse.getTeamAttachUrl(teamAttachUrl);
    }

    // 특정 팀 첨부 URL 삭제 By teamAttachUrlId
    public void deleteTeamAttachUrl(Long memberId, Long teamAttachUrlId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamAttachUrl teamAttachUrl = getTeamAttachUrl(teamAttachUrlId);

        // 해당 팀 첨부 URL 단일 삭제
        teamAttachUrlRepository.deleteById(teamAttachUrl.getId());

        // 해당 팀 첨부가 존재하지 않는다면
        if (!teamAttachUrlRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfile.updateIsTeamAttachUrl(false);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }
    }

    public void deleteAllTeamAttachUrl(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 빈 값 들어왔다고 무조건 제외하면 안되고, 상황 판단 이후에 제거해야 한다.
        // 기존에 존재했던 이력이 있으먄
        if (teamAttachUrlRepository.existsByTeamProfileId(teamProfile.getId())) {
            // 모든 팀 첨부 항목 삭제
            teamAttachUrlRepository.deleteAllByTeamProfileId(teamProfile.getId());
            teamProfile.updateIsTeamAttachUrl(false);
            log.info("teamProfile -> false로 변경");
            teamProfile.updateMemberTeamProfileTypeByCompletion();
            // 삭제한다.
        }

    }
}
