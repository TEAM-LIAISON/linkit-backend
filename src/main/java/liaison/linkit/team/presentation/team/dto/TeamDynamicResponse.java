package liaison.linkit.team.presentation.team.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamDynamicResponse {
    private String teamName;
    private String teamCode;
    private LocalDateTime createdAt;
}
