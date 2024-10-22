package liaison.linkit.login.business;

import java.time.LocalDateTime;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;

@Mapper
public class LoginMapper {
    
    public AccountResponseDTO.LogoutResponse toLogout() {
        return AccountResponseDTO.LogoutResponse.builder()
                .logoutAt(LocalDateTime.now())
                .build();
    }
}
