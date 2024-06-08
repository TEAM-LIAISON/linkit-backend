package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.service.TeamMiniProfileService;
import liaison.linkit.team.service.TeamProfileService;
import liaison.linkit.team.service.TeamProfileTeamBuildingFieldService;
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
@RequestMapping("/team_profile")
@Slf4j
public class TeamProfileController {

    final TeamProfileService teamProfileService;
    final TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;
    final TeamMiniProfileService teamMiniProfileService;

    // 팀 소개서 온보딩 과정에서 첫번째 항목
    @PostMapping("/field/basic-team")
    @MemberOnly
    public ResponseEntity<Void> createOnBoardingFirst(
            @Auth final Accessor accessor,
            @RequestBody @Valid final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest
    ) {
        // 일단 희망 팀빌딩 분야부터 처리
        teamProfileTeamBuildingFieldService.saveTeamBuildingField(accessor.getMemberId(), onBoardingFieldTeamInformRequest.getTeamBuildingFieldNames());

        // 미니 프로필에 있는 팀 제목, 규모, 분야 저장
        teamMiniProfileService.saveOnBoarding(accessor.getMemberId(), onBoardingFieldTeamInformRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
