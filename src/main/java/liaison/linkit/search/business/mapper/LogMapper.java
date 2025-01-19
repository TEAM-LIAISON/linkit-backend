package liaison.linkit.search.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenus;

@Mapper
public class LogMapper {
    public LogInformMenus toLogInformMenus() {
        return LogInformMenus.builder().build();
    }
}
