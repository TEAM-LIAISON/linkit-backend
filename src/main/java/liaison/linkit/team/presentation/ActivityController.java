//package liaison.linkit.team.presentation;
//
//import jakarta.validation.Valid;
//import liaison.linkit.auth.Auth;
//import liaison.linkit.auth.MemberOnly;
//import liaison.linkit.auth.domain.Accessor;
//import liaison.linkit.team.dto.request.activity.ActivityCreateRequest;
//import liaison.linkit.team.service.ActivityService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/activity-method")
//public class ActivityController {
//
//    private final ActivityService activityService;
//
//    // 활동 분야 및 지역 저장
//    @PostMapping
//    @MemberOnly
//    public ResponseEntity<Void> createActivity(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid ActivityCreateRequest activityCreateRequest
//    ) {
//        // 활동 방식은 활동 방식 테이블에 저장
//        activityService.saveActivityMethod(accessor.getMemberId(), activityCreateRequest);
//
//        // 활동 지역은 활동 지역 테이블에 저장
//        activityService.saveActivityRegion(accessor.getMemberId(), activityCreateRequest);
//
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//
//}
