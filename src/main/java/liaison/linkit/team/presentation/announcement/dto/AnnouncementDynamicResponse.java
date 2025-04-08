package liaison.linkit.team.presentation.announcement.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDynamicResponse {
    private String teamName;
    private String teamCode;
    private Long teamMemberAnnouncementId;
    private LocalDateTime createdAt;
}
