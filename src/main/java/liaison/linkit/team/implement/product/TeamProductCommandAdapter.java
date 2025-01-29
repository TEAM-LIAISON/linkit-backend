package liaison.linkit.team.implement.product;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.domain.repository.product.TeamProductRepository;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO.UpdateTeamProductRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamProductCommandAdapter {
    private final TeamProductRepository teamProductRepository;


    public TeamProduct addTeamProduct(final TeamProduct teamProduct) {
        return teamProductRepository.save(teamProduct);
    }

    public TeamProduct updateTeamProduct(final TeamProduct teamProduct, final UpdateTeamProductRequest updateTeamProductRequest) {
        return teamProductRepository.updateTeamProduct(teamProduct, updateTeamProductRequest);
    }

    public void removeTeamProduct(final TeamProduct teamProduct) {
        teamProductRepository.delete(teamProduct);
    }

    public void deleteAllTeamProducts(final Long teamId) {
        teamProductRepository.deleteAllByTeamId(teamId);
    }
}
