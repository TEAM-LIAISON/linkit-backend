package liaison.linkit.team.business.mapper.history;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.history.TeamHistory;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.AddTeamHistoryRequest;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.RemoveTeamHistoryResponse;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryCalendarResponse;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryItem;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryViewItem;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.UpdateTeamHistoryResponse;

@Mapper
public class TeamHistoryMapper {

    public TeamHistoryResponseDTO.TeamHistoryCalendarResponse toTeamHistoryCalendar(
            final Boolean isMyTeam, final List<TeamHistory> teamHistories) {
        List<TeamHistoryViewItem> teamHistoryViewItems =
                teamHistories.stream().map(this::toTeamHistoryViewItem).toList();

        // 2) 년도/월 그룹핑
        Map<String, Map<String, List<TeamHistoryViewItem>>> groupByYearMonth =
                groupByYearAndMonth(teamHistoryViewItems);

        // 3) 년도 및 월 모두 내림차순 정렬
        Map<String, Map<String, List<TeamHistoryViewItem>>> sortedGroupByYearMonth =
                new TreeMap<>(Comparator.reverseOrder());
        for (Map.Entry<String, Map<String, List<TeamHistoryViewItem>>> entry :
                groupByYearMonth.entrySet()) {
            // 내부 map(월)을 내림차순 정렬 (월 키가 String인 경우, "01", "02" ... "12" 형식이어야 올바른 순서가 보장됩니다)
            Map<String, List<TeamHistoryViewItem>> sortedMonthMap =
                    new TreeMap<>(Comparator.reverseOrder());
            sortedMonthMap.putAll(entry.getValue());
            sortedGroupByYearMonth.put(entry.getKey(), sortedMonthMap);
        }

        // 4) 원하는 JSON 형태로 변환
        List<Map<String, List<Map<String, List<TeamHistoryViewItem>>>>> finalCalendarStructure =
                convertToNestedList(sortedGroupByYearMonth);

        return TeamHistoryCalendarResponse.builder()
                .isMyTeam(isMyTeam)
                .teamHistoryCalendar(finalCalendarStructure)
                .build();
    }

    public TeamHistoryViewItem toTeamHistoryViewItem(final TeamHistory teamHistory) {
        return TeamHistoryResponseDTO.TeamHistoryViewItem.builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .historyDescription(teamHistory.getHistoryDescription())
                .build();
    }

    public TeamHistoryResponseDTO.TeamHistoryItems toTeamHistoryItems(
            List<TeamHistory> teamHistories) {
        List<TeamHistoryItem> items = teamHistories.stream().map(this::toTeamHistoryItem).toList();

        return TeamHistoryResponseDTO.TeamHistoryItems.builder().teamHistoryItems(items).build();
    }

    public TeamHistoryResponseDTO.TeamHistoryItem toTeamHistoryItem(final TeamHistory teamHistory) {
        return TeamHistoryResponseDTO.TeamHistoryItem.builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .build();
    }

    public TeamHistoryResponseDTO.TeamHistoryDetail toTeamHistoryDetail(
            final TeamHistory teamHistory) {
        return TeamHistoryResponseDTO.TeamHistoryDetail.builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .historyDescription(teamHistory.getHistoryDescription())
                .build();
    }

    public TeamHistory toAddTeamHistory(
            final Team team, final AddTeamHistoryRequest addTeamHistoryRequest) {
        return TeamHistory.builder()
                .id(null)
                .team(team)
                .historyName(addTeamHistoryRequest.getHistoryName())
                .historyStartDate(addTeamHistoryRequest.getHistoryStartDate())
                .historyEndDate(addTeamHistoryRequest.getHistoryEndDate())
                .isHistoryInProgress(addTeamHistoryRequest.getIsHistoryInProgress())
                .historyDescription(addTeamHistoryRequest.getHistoryDescription())
                .build();
    }

    public TeamHistoryResponseDTO.AddTeamHistoryResponse toAddTeamHistoryResponse(
            final TeamHistory teamHistory) {
        return TeamHistoryResponseDTO.AddTeamHistoryResponse.builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .historyDescription(teamHistory.getHistoryDescription())
                .build();
    }

    public TeamHistoryResponseDTO.UpdateTeamHistoryResponse toUpdateTeamHistoryResponse(
            final TeamHistory teamHistory) {
        return UpdateTeamHistoryResponse.builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .historyDescription(teamHistory.getHistoryDescription())
                .build();
    }

    public TeamHistoryResponseDTO.RemoveTeamHistoryResponse toRemoveTeamHistory(
            final Long teamHistoryId) {
        return RemoveTeamHistoryResponse.builder().teamHistoryId(teamHistoryId).build();
    }

    private Map<String, Map<String, List<TeamHistoryViewItem>>> groupByYearAndMonth(
            final List<TeamHistoryViewItem> teamHistoryViewItems) {
        // year -> (month -> List<item>) 구조
        Map<String, Map<String, List<TeamHistoryViewItem>>> map = new LinkedHashMap<>();

        for (TeamHistoryViewItem teamHistoryViewItem : teamHistoryViewItems) {
            // startDate = "2023.01" 형태로 가정
            String[] dateParts = teamHistoryViewItem.getHistoryStartDate().split("\\.");
            // dateParts[0] = "2023", dateParts[1] = "01"
            String year = dateParts[0];
            String month = dateParts[1];

            // year가 없으면 새로 put
            map.putIfAbsent(year, new LinkedHashMap<>());

            // month가 없으면 새 list
            map.get(year).putIfAbsent(month, new ArrayList<>());

            // list에 현재 item 추가
            map.get(year).get(month).add(teamHistoryViewItem);
        }

        return map;
    }

    private List<Map<String, List<Map<String, List<TeamHistoryViewItem>>>>> convertToNestedList(
            Map<String, Map<String, List<TeamHistoryViewItem>>> groupByYearMonth) {

        List<Map<String, List<Map<String, List<TeamHistoryViewItem>>>>> result = new ArrayList<>();

        for (String year : groupByYearMonth.keySet()) {
            List<Map<String, List<TeamHistoryViewItem>>> monthList = new ArrayList<>();
            Map<String, List<TeamHistoryViewItem>> monthMap = groupByYearMonth.get(year);

            for (String month : monthMap.keySet()) {
                Map<String, List<TeamHistoryViewItem>> singleMonthEntry = new LinkedHashMap<>();
                singleMonthEntry.put(month, monthMap.get(month));
                monthList.add(singleMonthEntry);
            }

            Map<String, List<Map<String, List<TeamHistoryViewItem>>>> singleYearEntry =
                    new LinkedHashMap<>();
            singleYearEntry.put(year, monthList);

            result.add(singleYearEntry);
        }

        return result;
    }
}
