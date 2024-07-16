package liaison.linkit.team.dto.request.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamMemberAnnouncementRequest {

    // 1. 직무/역할
    private final List<String> jobRoleNames;

    // 2. 주요 업무
    private final String mainBusiness;

    // 3. 요구 기술
    private final List<String> skillNames;

    // 4. 지원 절차
    private final String applicationProcess;
}
