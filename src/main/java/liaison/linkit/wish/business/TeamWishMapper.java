package liaison.linkit.wish.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.presentation.dto.teamScrap.TeamScrapResponseDTO;

@Mapper
public class TeamWishMapper {

    public TeamScrapResponseDTO.AddTeamWish toAddTeamWish(final TeamWish teamWish) {
        return TeamScrapResponseDTO.AddTeamWish.builder()
                .createdAt(teamWish.getCreatedAt())
                .build();
    }

    public TeamScrapResponseDTO.RemoveTeamWish toRemoveTeamWish() {
        return TeamScrapResponseDTO.RemoveTeamWish.builder()
                .build();
    }
}
