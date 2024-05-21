package liaison.linkit.team.dto.request.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class BothActivityMethodCreateRequest {

    // 지역 이름
    private final String regionName;
    // 활동 시간 관련된거 스트링 값으로 전달 받기
    private final String activityTagName;
}
