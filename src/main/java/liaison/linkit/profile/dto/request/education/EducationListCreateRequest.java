package liaison.linkit.profile.dto.request.education;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class EducationListCreateRequest {
    @NotNull(message = "교육 리스트를 입력해주세요")
    private List<EducationCreateRequest> educationList;

    public EducationListCreateRequest(List<EducationCreateRequest> educationList) {
        this.educationList = educationList;
    }
}
