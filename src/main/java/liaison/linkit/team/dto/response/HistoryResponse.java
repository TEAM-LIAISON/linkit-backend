package liaison.linkit.team.dto.response;

import liaison.linkit.team.domain.History;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HistoryResponse {

    private final String historyOneLineIntroduction;
    private final int startYear;
    private final int startMonth;
    private final int endYear;
    private final int endMonth;
    private final String historyIntroduction;
    private final boolean inProgress;

    public static HistoryResponse personalHistory(final History history) {
        return new HistoryResponse(
                history.getHistoryOneLineIntroduction(),
                history.getStartYear(),
                history.getStartMonth(),
                history.getEndYear(),
                history.getEndMonth(),
                history.getHistoryIntroduction(),
                history.isInProgress()
        );
    }

    public static HistoryResponse of(final History history) {
        return new HistoryResponse(
                history.getHistoryOneLineIntroduction(),
                history.getStartYear(),
                history.getStartMonth(),
                history.getEndYear(),
                history.getEndMonth(),
                history.getHistoryIntroduction(),
                history.isInProgress()
        );
    }
}
