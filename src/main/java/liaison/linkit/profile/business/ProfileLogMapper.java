package liaison.linkit.profile.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.ProfileLog;
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
                .build();
    }

    public RemoveProfileLogResponse toRemoveProfileLog(final Long memberId, final Long profileLogId) {
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


    public AddProfileLogBodyImageResponse toAddProfileLogBodyImageResponse(final String profileLogBodyImagePath) {
        return AddProfileLogBodyImageResponse
                .builder()
                .profileLogBodyImagePath(profileLogBodyImagePath)
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
