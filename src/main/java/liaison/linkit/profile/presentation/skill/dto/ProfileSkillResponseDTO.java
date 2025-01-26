package liaison.linkit.profile.presentation.skill.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileSkillResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileSkillItem {
        private Long profileSkillId;
        private String skillName;
        private String skillLevel;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileSkillItems {
        @Builder.Default
        private List<ProfileSkillItem> profileSkillItems = List.of();
    }
}
