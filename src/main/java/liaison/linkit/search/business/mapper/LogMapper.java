package liaison.linkit.search.business.mapper;

import java.util.List;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenu;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenus;

@Mapper
public class LogMapper {
    public LogInformMenus toLogInformMenus(final List<LogInformMenu> logInformMenus) {
        return LogInformMenus.builder().logInformMenus(logInformMenus).build();
    }
}
