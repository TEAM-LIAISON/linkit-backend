package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.dto.request.activity.ActivityCreateRequest;
import liaison.linkit.team.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static liaison.linkit.global.exception.ExceptionCode.HAVE_TO_INPUT_ACTIVITY_TAG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ActivityController {

    private final ActivityService activityService;

    // 활동 분야 및 지역 저장/수정
    // 팀 소개서 온보딩 과정 2번째
    @PostMapping("/team/activity")
    @MemberOnly
    public ResponseEntity<Void> createActivity(
            @Auth final Accessor accessor,
            @RequestBody @Valid ActivityCreateRequest activityCreateRequest
    ) {
        if (activityCreateRequest.getActivityTagNames().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_ACTIVITY_TAG_NAME);
        }
        
        // 활동 방식은 활동 방식 테이블에 저장
        activityService.saveActivityMethod(accessor.getMemberId(), activityCreateRequest);
        // 활동 지역은 활동 지역 테이블에 저장
        activityService.saveActivityRegion(accessor.getMemberId(), activityCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
