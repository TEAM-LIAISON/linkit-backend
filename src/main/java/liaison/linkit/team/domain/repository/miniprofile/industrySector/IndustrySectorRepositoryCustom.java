package liaison.linkit.team.domain.repository.miniprofile.industrySector;

import liaison.linkit.team.domain.miniprofile.IndustrySector;

public interface IndustrySectorRepositoryCustom {
    IndustrySector findBySectorName(final String sectorName);
}
