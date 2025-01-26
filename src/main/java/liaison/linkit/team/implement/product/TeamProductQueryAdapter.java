package liaison.linkit.team.implement.product;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.domain.repository.product.TeamProductRepository;
import liaison.linkit.team.exception.product.TeamProductNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamProductQueryAdapter {

    private final TeamProductRepository teamProductRepository;

    public List<TeamProduct> getTeamProducts(final Long teamId) {
        return teamProductRepository.getTeamProducts(teamId);
    }

    public TeamProduct getTeamProduct(final Long teamProductId) {
        return teamProductRepository.getTeamProduct(teamProductId)
                .orElseThrow(() -> TeamProductNotFoundException.EXCEPTION);
    }
}
