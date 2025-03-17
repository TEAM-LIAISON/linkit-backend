package liaison.linkit.team.business.service.log;

import static liaison.linkit.profile.domain.type.LogType.GENERAL_LOG;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.image.domain.Image;
import liaison.linkit.image.implement.ImageCommandAdapter;
import liaison.linkit.image.implement.ImageQueryAdapter;
import liaison.linkit.image.util.ImageUtils;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.team.business.mapper.log.TeamLogMapper;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.log.TeamLogImage;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.exception.log.UpdateTeamLogTypeBadRequestException;
import liaison.linkit.team.exception.teamMember.TeamAdminNotRegisteredException;
import liaison.linkit.team.implement.log.TeamLogCommandAdapter;
import liaison.linkit.team.implement.log.TeamLogImageCommandAdapter;
import liaison.linkit.team.implement.log.TeamLogImageQueryAdapter;
import liaison.linkit.team.implement.log.TeamLogQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogRequest;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.AddTeamLogBodyImageResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.AddTeamLogResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.RemoveTeamLogResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.TeamLogItem;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.UpdateTeamLogPublicStateResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.UpdateTeamLogTypeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamLogService {

    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamLogQueryAdapter teamLogQueryAdapter;
    private final TeamLogCommandAdapter teamLogCommandAdapter;
    private final TeamLogMapper teamLogMapper;

    private final TeamLogImageCommandAdapter teamLogImageCommandAdapter;
    private final TeamLogImageQueryAdapter teamLogImageQueryAdapter;

    private final ImageQueryAdapter imageQueryAdapter;
    private final ImageCommandAdapter imageCommandAdapter;
    private final ImageValidator imageValidator;

    private final ImageUtils imageUtils;

    private final S3Uploader s3Uploader;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    @Transactional(readOnly = true)
    public TeamLogResponseDTO.TeamLogItems getTeamLogItems(
            final Optional<Long> optionalMemberId, final String teamCode) {
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        boolean isOwnerOrManager = false;
        List<TeamLog> teamLogs;
        if (optionalMemberId.isPresent()) {
            isOwnerOrManager =
                    teamMemberQueryAdapter.isOwnerOrManagerOfTeam(
                            team.getId(), optionalMemberId.get());

            if (isOwnerOrManager) {
                teamLogs = teamLogQueryAdapter.getTeamLogs(team.getId());
            } else {
                teamLogs = teamLogQueryAdapter.getTeamLogsPublic(team.getId());
            }
        } else {
            teamLogs = teamLogQueryAdapter.getTeamLogsPublic(team.getId());
        }

        return teamLogMapper.toTeamLogItems(isOwnerOrManager, teamLogs);
    }

    @Transactional
    public TeamLogItem getTeamLogItem(
            final Optional<Long> optionalMemberId, final String teamCode, final Long teamLogId) {
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);
        teamLog.increaseViewCount();

        Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        boolean isTeamManager =
                optionalMemberId
                        .map(
                                memberId ->
                                        teamMemberQueryAdapter.isOwnerOrManagerOfTeam(
                                                targetTeam.getId(), memberId))
                        .orElse(false);

        return teamLogMapper.toTeamLogItem(isTeamManager, teamLog);
    }

    @Transactional(readOnly = true)
    public TeamLogResponseDTO.TeamLogRepresentItem getRepresentTeamLogItem(
            final Optional<Long> optionalMemberId, final String teamCode) {
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        boolean isTeamManager =
                optionalMemberId
                        .map(
                                memberId ->
                                        teamMemberQueryAdapter.isOwnerOrManagerOfTeam(
                                                targetTeam.getId(), memberId))
                        .orElse(false);

        List<TeamLog> teamLogs = List.of();

        if (teamLogQueryAdapter.existsRepresentativePublicTeamLogByTeam(targetTeam.getId())) {
            TeamLog teamLog =
                    teamLogQueryAdapter.getRepresentativePublicTeamLog(targetTeam.getId());
            teamLogs = List.of(teamLog);
        }

        return teamLogMapper.toTeamLogRepresentItem(isTeamManager, teamLogs);
    }

    // 팀 로그 본문 이미지 추가
    public AddTeamLogBodyImageResponse addTeamLogBodyImage(
            final Long memberId, final String teamCode, final MultipartFile teamLogBodyImage) {
        String teamLogBodyImagePath = null;

        // 버켓에만 저장함
        if (imageValidator.validatingImageUpload(teamLogBodyImage)) {
            teamLogBodyImagePath =
                    s3Uploader.uploadTeamLogBodyImage(new ImageFile(teamLogBodyImage));
        }

        // DB에 이미지 저장
        final Image image = new Image(null, teamLogBodyImagePath, true, LocalDateTime.now());
        final Image savedImage = imageCommandAdapter.addImage(image);

        return teamLogMapper.toAddTeamLogBodyImageResponse(savedImage);
    }

    // 팀 로그 생성
    public AddTeamLogResponse addTeamLog(
            final Long memberId,
            final String teamCode,
            final TeamLogRequestDTO.AddTeamLogRequest addTeamLogRequest) {
        // 1. 팀 조회
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(team.getId(), memberId)) {
            throw TeamAdminNotRegisteredException.EXCEPTION;
        }

        LogType addTeamLogType = GENERAL_LOG;

        if (!teamLogQueryAdapter.existsRepresentativeTeamLogByTeam(team.getId())) {
            addTeamLogType = LogType.REPRESENTATIVE_LOG;
        }

        // 2. TeamLog 엔티티 생성 및 저장
        final TeamLog teamLog =
                new TeamLog(
                        null,
                        team,
                        addTeamLogRequest.getLogTitle(),
                        addTeamLogRequest.getLogContent(),
                        addTeamLogRequest.getIsLogPublic(),
                        addTeamLogType,
                        0L);

        final TeamLog savedTeamLog = teamLogCommandAdapter.addTeamLog(teamLog);

        final List<String> teamLogImagePaths =
                imageUtils.extractImageUrls(addTeamLogRequest.getLogContent());

        if (!teamLogImagePaths.isEmpty()) {
            final List<Image> images = imageQueryAdapter.findByImageUrls(teamLogImagePaths);

            for (Image image : images) {
                TeamLogImage teamLogImage =
                        TeamLogImage.builder().teamLog(savedTeamLog).image(image).build();

                teamLogImageCommandAdapter.addTeamLogImage(teamLogImage);

                image.setTemporary(false);
            }
        }

        return teamLogMapper.toAddTeamLogResponse(savedTeamLog);
    }

    // 팀 로그 수정
    public TeamLogResponseDTO.UpdateTeamLogResponse updateTeamLog(
            final Long memberId,
            final String teamCode,
            final Long teamLogId,
            final UpdateTeamLogRequest updateTeamLogRequest) {
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);

        final TeamLog updatedTeamLog =
                teamLogCommandAdapter.updateTeamLog(teamLog, updateTeamLogRequest);

        final List<String> teamLogImagePaths =
                imageUtils.extractImageUrls(updateTeamLogRequest.getLogContent());

        final List<Image> images = imageQueryAdapter.findByImageUrls(teamLogImagePaths);

        final List<TeamLogImage> existingTeamLogImages =
                teamLogImageQueryAdapter.findByTeamLog(teamLog);

        // 현재 사용되는 이미지 ID
        Set<Long> currentImageIds = images.stream().map(Image::getId).collect(Collectors.toSet());

        // 기존에 사용되던 이미지 ID
        Set<Long> existingImageIds =
                existingTeamLogImages.stream()
                        .map(teamLogImage -> teamLogImage.getImage().getId())
                        .collect(Collectors.toSet());

        // 새로운 이미지 ID
        Set<Long> newImageIds = new HashSet<>(currentImageIds);
        newImageIds.removeAll(existingImageIds);

        // 삭제된 이미지 ID
        Set<Long> removedImageIds = new HashSet<>(existingImageIds);
        removedImageIds.removeAll(currentImageIds);

        // 새로운 이미지에 대해 ProfileLogImage 생성 및 이미지 상태 업데이트
        for (Image image : images) {
            if (newImageIds.contains(image.getId())) {
                TeamLogImage teamLogImage =
                        TeamLogImage.builder().teamLog(teamLog).image(image).build();
                teamLogImageCommandAdapter.addTeamLogImage(teamLogImage);

                // 이미지의 isTemporary를 false로 업데이트
                image.setTemporary(false);
                imageCommandAdapter.addImage(image);
            }
        }

        // 삭제된 이미지에 대한 ProfileLogImage 관계 제거
        for (TeamLogImage teamLogImage : existingTeamLogImages) {
            if (removedImageIds.contains(teamLogImage.getImage().getId())) {
                teamLogImageCommandAdapter.removeTeamLogImage(teamLogImage);
            }
        }

        return teamLogMapper.toUpdateTeamLogResponse(updatedTeamLog);
    }

    // 팀 로그 삭제
    public RemoveTeamLogResponse removeTeamLog(
            final Long memberId, final String teamCode, final Long teamLogId) {
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);
        List<TeamLogImage> teamLogImages = teamLogImageQueryAdapter.findByTeamLog(teamLog);

        for (TeamLogImage teamLogImage : teamLogImages) {
            Image image = teamLogImage.getImage();

            teamLogImageCommandAdapter.removeTeamLogImage(teamLogImage);

            image.setTemporary(true);
            imageCommandAdapter.addImage(image);
        }

        teamLogCommandAdapter.remove(teamLog);

        return teamLogMapper.toRemoveTeamLog(teamLogId);
    }

    // 팀 로그 대표글로 수정
    public UpdateTeamLogTypeResponse updateTeamLogType(
            final Long memberId, final String teamCode, final Long teamLogId) {
        // 1. TeamLog 엔티티 조회
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);

        // 2. 현재 프로필 조회
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);

        if (!teamLog.isLogPublic()) {
            throw UpdateTeamLogTypeBadRequestException.EXCEPTION;
        }

        // 3. 기존 대표 로그 조회
        TeamLog existingRepresentativeTeamLog = null;
        if (teamLogQueryAdapter.existsRepresentativeTeamLogByTeam(team.getId())) {
            existingRepresentativeTeamLog =
                    teamLogQueryAdapter.getRepresentativeTeamLog(team.getId());
        }

        if (existingRepresentativeTeamLog != null
                && !existingRepresentativeTeamLog.getId().equals(teamLogId)) {
            teamLogCommandAdapter.updateTeamLogTypeGeneral(existingRepresentativeTeamLog);
        }

        teamLogCommandAdapter.updateTeamLogTypeRepresent(teamLog);
        return teamLogMapper.toUpdateTeamLogType(teamLog);
    }

    // 팀 로그 공개 여부 수정
    public UpdateTeamLogPublicStateResponse updateTeamLogPublicState(
            final Long memberId, final String teamCode, final Long teamLogId) {
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);

        //        if (teamLog.getLogType().equals(LogType.REPRESENTATIVE_LOG)) {
        //            throw UpdateTeamLogPublicBadRequestException.EXCEPTION;
        //        }

        final boolean isTeamLogCurrentPublicState = teamLog.isLogPublic();
        final TeamLog updatedTeamLog =
                teamLogCommandAdapter.updateTeamLogPublicState(
                        teamLog, isTeamLogCurrentPublicState);

        return teamLogMapper.toUpdateTeamLogPublicState(updatedTeamLog);
    }
}
