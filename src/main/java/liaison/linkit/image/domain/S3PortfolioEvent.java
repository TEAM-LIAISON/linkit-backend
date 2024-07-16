package liaison.linkit.image.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class S3PortfolioEvent {
    private final String portfolioName;
}
