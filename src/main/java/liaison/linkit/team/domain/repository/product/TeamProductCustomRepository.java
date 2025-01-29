package liaison.linkit.team.domain.repository.product;

import java.util.List;
import java.util.Optional;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO.UpdateTeamProductRequest;

public interface TeamProductCustomRepository {

    List<TeamProduct> getTeamProducts(final Long teamId);

    Optional<TeamProduct> getTeamProduct(final Long teamProductId);

    TeamProduct updateTeamProduct(final TeamProduct teamProduct, final UpdateTeamProductRequest updateTeamProductRequest);

    void deleteAllByTeamId(final Long teamId);
}
