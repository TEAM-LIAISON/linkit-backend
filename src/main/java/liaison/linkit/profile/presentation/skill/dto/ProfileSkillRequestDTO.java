package liaison.linkit.profile.presentation.skill.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileSkillRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileSkillRequest {
        @NotEmpty(message = "스킬 목록은 비어있을 수 없습니다")
        @Valid
        @Builder.Default
        private List<AddProfileSkillItem> profileSkillItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileSkillItem {
        @NotEmpty(message = "스킬 이름은 필수입니다")
        private String skillName;

        @NotEmpty(message = "스킬 레벨은 필수입니다")
        private String skillLevel;
    }
}
