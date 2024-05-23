package liaison.linkit.profile.dto.csv;

import liaison.linkit.profile.domain.education.University;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UniversityDto {
    private String universityName;

    public University toEntity() {
        return University.builder()
                .universityName(this.universityName)
                .build();
    }

}
