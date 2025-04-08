package liaison.linkit.team.presentation.log.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamLogDynamicResponse {
    private String teamName;
    private String teamCode;
    private Long teamLogId;
    private LocalDateTime createdAt;
}
