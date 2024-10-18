package liaison.linkit.profile.business;

import java.time.LocalDateTime;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.presentation.url.dto.ProfileUrlResponseDTO;

@Mapper
public class ProfileUrlMapper {
    // 삭제 응답 매핑
    public ProfileUrlResponseDTO.RemoveProfileUrl toRemoveProfileUrl(final Long profileUrlId) {
        return ProfileUrlResponseDTO.RemoveProfileUrl.builder()
                .profileUrlId(profileUrlId)
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
