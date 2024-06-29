package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.request.onBoarding.personal.OnBoardingPersonalJobAndSkillCreateRequest;
import liaison.linkit.profile.service.onBoarding.OnBoardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onBoarding")
@Slf4j
public class OnBoardingController {

    public final MemberService memberService;
    public final OnBoardingService onBoardingService;

    @PostMapping("/private/job/skill")
    @MemberOnly
    public ResponseEntity<Void> createOnBoardingPersonalJobAndSkill(
            @Auth final Accessor accessor,
            @RequestBody @Valid final OnBoardingPersonalJobAndSkillCreateRequest createRequest
    ) {
        onBoardingService.savePersonalJobAndRole(accessor.getMemberId(), createRequest.getJobRoleNames());
        onBoardingService.savePersonalSkill(accessor.getMemberId(), createRequest.getSkillNames());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
