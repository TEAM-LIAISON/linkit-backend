package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.dto.request.memberIntroduction.TeamMemberIntroductionCreateRequest;
import liaison.linkit.team.service.TeamMemberIntroductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.HAVE_TO_INPUT_TEAM_MEMBER_INTRODUCTION;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TeamMemberIntroductionController {

    private final TeamMemberIntroductionService teamMemberIntroductionService;

    @PostMapping("/team/member")
    @MemberOnly
    public ResponseEntity<Void> createTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        if (teamMemberIntroductionCreateRequest.getTeamMemberIntroductionText().isEmpty() ||
                teamMemberIntroductionCreateRequest.getTeamMemberName().isEmpty() ||
                teamMemberIntroductionCreateRequest.getTeamMemberRole().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_TEAM_MEMBER_INTRODUCTION);
        }

        teamMemberIntroductionService.saveTeamMemberIntroduction(accessor.getMemberId(), teamMemberIntroductionCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 수정
    @PostMapping("/team/member/{teamMemberIntroductionId}")
    @MemberOnly
    public ResponseEntity<Void> updateTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberIntroductionId,
            @RequestBody @Valid TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        teamMemberIntroductionService.validateTeamMemberIntroductionByMember(accessor.getMemberId());
        teamMemberIntroductionService.updateTeamMemberIntroduction(teamMemberIntroductionId, teamMemberIntroductionCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/team/members")
    @MemberOnly
    public ResponseEntity<Void> createTeamMemberIntroductions(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequests
    ) {
        teamMemberIntroductionService.saveTeamMemberIntroductions(accessor.getMemberId(), teamMemberIntroductionCreateRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 단일 팀원 소개 삭제
    @DeleteMapping("/team/members/{teamMemberIntroductionId}")
    @MemberOnly
    public ResponseEntity<Void> deleteTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberIntroductionId
    ) {
        teamMemberIntroductionService.validateTeamMemberIntroductionByMember(accessor.getMemberId());
        teamMemberIntroductionService.deleteTeamMemberIntroduction(accessor.getMemberId(), teamMemberIntroductionId);
        return ResponseEntity.noContent().build();
    }
}

