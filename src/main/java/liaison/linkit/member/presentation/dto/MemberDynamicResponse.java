package liaison.linkit.member.presentation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDynamicResponse {
    private String emailId;
    private String memberName;
    private LocalDateTime createdAt;
}
