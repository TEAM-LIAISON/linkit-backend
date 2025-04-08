package liaison.linkit.profile.presentation.log.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileLogDynamicResponse {
    private String emailId;
    private String memberName;
    private Long profileLogId;
    private LocalDateTime createdAt;
}
