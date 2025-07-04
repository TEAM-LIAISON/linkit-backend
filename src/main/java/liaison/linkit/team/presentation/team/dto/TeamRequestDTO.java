package liaison.linkit.team.presentation.team.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamRequest {

        @NotBlank(message = "팀 이름을 입력해주세요.")
        @Size(min = 1, max = 10, message = "팀 이름은 1자 이상 10자 이하여야 합니다")
        private String teamName;

        @NotBlank(message = "팀 아이디를 입력해주세요.")
        private String teamCode;

        @NotBlank(message = "팀 한 줄 소개를 입력해주세요.")
        private String teamShortDescription;

        @NotBlank(message = "팀 규모를 선택해주세요.")
        private String scaleName;

        @NotBlank(message = "활동지역 시/도를 선택해주세요.")
        private String cityName;

        @NotBlank(message = "활동지역 시/군/구를 선택해주세요.")
        private String divisionName;

        @NotEmpty(message = "팀 현재상태를 선택해주세요.")
        private List<String> teamStateNames;

        @NotNull(message = "팀 공개여부를 선택해주세요.")
        private Boolean isTeamPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamRequest {

        @NotBlank(message = "팀 이름을 입력해주세요.")
        @Size(min = 1, max = 10, message = "팀 이름은 1자 이상 10자 이하여야 합니다")
        private String teamName;

        @NotBlank(message = "팀 아이디를 입력해주세요.")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9]*$",
                message = "팀 아이디는 영어로 시작해야 하며, 영어 대소문자와 숫자만 사용할 수 있습니다.")
        private String teamCode;

        @NotBlank(message = "팀 한 줄 소개를 입력해주세요.")
        private String teamShortDescription;

        @NotBlank(message = "팀 규모를 선택해주세요.")
        private String scaleName;

        @NotBlank(message = "활동지역 시/도를 선택해주세요.")
        private String cityName;

        @NotBlank(message = "활동지역 시/군/구를 선택해주세요.")
        private String divisionName;

        @NotEmpty(message = "팀 현재상태를 선택해주세요.")
        private List<String> teamStateNames;

        @NotNull(message = "팀 공개여부를 선택해주세요.")
        private Boolean isTeamPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateManagingTeamStateRequest {

        private TeamMemberManagingTeamState teamMemberManagingTeamState;
    }
}
