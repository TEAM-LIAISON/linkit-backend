package liaison.linkit.team.dto.response.history;

import liaison.linkit.team.domain.history.History;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HistoryResponse {

    private final String historyOneLineIntroduction;
    private final int startYear;
    private final int endYear;
    private final String historyIntroduction;
    private final boolean inProgress;

    public static HistoryResponse of(final History history) {
        return new HistoryResponse(
                history.getHistoryOneLineIntroduction(),
                history.getStartYear(),
                history.getEndYear(),
                history.getHistoryIntroduction(),
                history.isInProgress()
        );
    }
}
