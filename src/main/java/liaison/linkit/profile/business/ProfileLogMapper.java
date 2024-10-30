package liaison.linkit.profile.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;

@Mapper
public class ProfileLogMapper {

    public ProfileLogResponseDTO.ProfileLogItem toProfileLogItem(final ProfileLog profileLog) {
        return ProfileLogResponseDTO.ProfileLogItem.builder()
                .profileLogId(profileLog.getId())
                .isLogPublic(profileLog.isLogPublic())
                .profileLogType(profileLog.getProfileLogType())
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

}
