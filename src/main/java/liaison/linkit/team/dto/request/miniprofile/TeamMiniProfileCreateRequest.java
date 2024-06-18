package liaison.linkit.team.dto.request.miniprofile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TeamMiniProfileCreateRequest {

    @NotNull(message = "팀 소개서 제목을 입력해주세요")
    private final String teamProfileTitle;

    @NotNull(message = "팀 소개서 업로드 기간을 설정해주세요")
    // 프로필 업로드 기간
    private final LocalDate teamUploadPeriod;

    @NotNull(message = "업로드 마감 여부를 선택해주세요")
    private final boolean teamUploadDeadline;

    @NotNull(message = "팀을 홍보할 수 있는 가치를 써주세요")
    private final String teamValue;

    @NotNull(message = "팀 세부 정보를 알려주세요")
    private final String teamDetailInform;
}
