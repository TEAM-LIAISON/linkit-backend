package liaison.linkit.team.presentation.teamMember.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberItems {
        @Builder.Default
        private List<TeamMemberItem> teamMemberItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberItem {
        private Long teamMemberId;
    }
}
