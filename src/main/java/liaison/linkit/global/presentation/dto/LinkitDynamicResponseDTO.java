package liaison.linkit.global.presentation.dto;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.member.presentation.dto.MemberDynamicResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogDynamicResponse;
import liaison.linkit.team.presentation.announcement.dto.AnnouncementDynamicResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogDynamicResponse;
import liaison.linkit.team.presentation.team.dto.TeamDynamicResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LinkitDynamicResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDynamicListResponse {
        @Builder.Default
        private List<MemberDynamicResponse> memberDynamicResponses = new ArrayList<>();

        public static MemberDynamicListResponse of(
                List<MemberDynamicResponse> memberDynamicResponses) {
            return MemberDynamicListResponse.builder()
                    .memberDynamicResponses(memberDynamicResponses)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamDynamicListResponse {
        @Builder.Default private List<TeamDynamicResponse> teamDynamicResponses = new ArrayList<>();

        public static TeamDynamicListResponse of(List<TeamDynamicResponse> teamDynamicResponses) {
            return TeamDynamicListResponse.builder()
                    .teamDynamicResponses(teamDynamicResponses)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementDynamicListResponse {
        @Builder.Default
        private List<AnnouncementDynamicResponse> announcementDynamicResponses = new ArrayList<>();

        public static AnnouncementDynamicListResponse of(
                List<AnnouncementDynamicResponse> announcementDynamicResponses) {
            return AnnouncementDynamicListResponse.builder()
                    .announcementDynamicResponses(announcementDynamicResponses)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLogDynamicListResponse {
        @Builder.Default
        private List<ProfileLogDynamicResponse> profileLogDynamicResponses = new ArrayList<>();

        public static ProfileLogDynamicListResponse of(
                List<ProfileLogDynamicResponse> profileLogDynamicResponses) {
            return ProfileLogDynamicListResponse.builder()
                    .profileLogDynamicResponses(profileLogDynamicResponses)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamLogDynamicListResponse {
        @Builder.Default
        private List<TeamLogDynamicResponse> teamLogDynamicResponses = new ArrayList<>();

        public static TeamLogDynamicListResponse of(
                List<TeamLogDynamicResponse> teamLogDynamicResponses) {
            return TeamLogDynamicListResponse.builder()
                    .teamLogDynamicResponses(teamLogDynamicResponses)
                    .build();
        }
    }
}
