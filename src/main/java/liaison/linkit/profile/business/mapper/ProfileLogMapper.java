package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.image.domain.Image;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogBodyImageResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.RemoveProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogResponse;

@Mapper
public class ProfileLogMapper {

    public ProfileLogResponseDTO.ProfileLogItem toProfileLogItem(final ProfileLog profileLog) {
        return ProfileLogResponseDTO.ProfileLogItem.builder()
                .profileLogId(profileLog.getId())
                .isLogPublic(profileLog.isLogPublic())
                .logType(profileLog.getLogType())
                .modifiedAt(profileLog.getModifiedAt())
                .logTitle(profileLog.getLogTitle())
                .logContent(profileLog.getLogContent())
                .build();
    }

    public ProfileLogResponseDTO.ProfileLogItems toProfileLogItems(final List<ProfileLog> profileLogs) {
        List<ProfileLogItem> items = profileLogs.stream()
                .map(this::toProfileLogItem)
                .collect(Collectors.toList());

        return ProfileLogResponseDTO.ProfileLogItems.builder()
                .profileLogItems(items)
                .build();
    }

    public ProfileLogResponseDTO.AddProfileLogResponse toAddProfileLogResponse(final ProfileLog profileLog) {
        return ProfileLogResponseDTO.AddProfileLogResponse
                .builder()
                .profileLogId(profileLog.getId())
                .logTitle(profileLog.getLogTitle())
                .logContent(profileLog.getLogContent())
                .createdAt(profileLog.getCreatedAt())
                .logType(profileLog.getLogType())
                .isLogPublic(profileLog.isLogPublic())
                .build();
    }

    public RemoveProfileLogResponse toRemoveProfileLog(final Long profileLogId) {
        return ProfileLogResponseDTO.RemoveProfileLogResponse
                .builder()
                .profileLogId(profileLogId)
                .build();
    }

    public ProfileLogResponseDTO.UpdateProfileLogTypeResponse toUpdateProfileLogType(final ProfileLog profileLog) {
        return ProfileLogResponseDTO.UpdateProfileLogTypeResponse
                .builder()
                .profileLogId(profileLog.getId())
                .logType(profileLog.getLogType())
                .build();
    }

    public ProfileLogResponseDTO.UpdateProfileLogPublicStateResponse toUpdateProfileLogPublicState(final ProfileLog profileLog) {
        return ProfileLogResponseDTO.UpdateProfileLogPublicStateResponse
                .builder()
                .profileLogId(profileLog.getId())
                .isLogPublic(profileLog.isLogPublic())
                .build();
    }


    public AddProfileLogBodyImageResponse toAddProfileLogBodyImageResponse(final Image image) {
        return AddProfileLogBodyImageResponse
                .builder()
                .profileLogBodyImagePath(image.getImageUrl())
                .build();
    }

    public UpdateProfileLogResponse toUpdateProfileLogResponse(final ProfileLog profileLog) {
        return UpdateProfileLogResponse
                .builder()
                .profileLogId(profileLog.getId())
                .logTitle(profileLog.getLogTitle())
                .logContent(profileLog.getLogContent())
                .isLogPublic(profileLog.isLogPublic())
                .logType(profileLog.getLogType())
                .build();
    }
}
