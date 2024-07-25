package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.dto.request.memberIntroduction.TeamMemberIntroductionCreateRequest;
import liaison.linkit.team.service.TeamMemberIntroductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.HAVE_TO_INPUT_TEAM_MEMBER_INTRODUCTION;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TeamMemberIntroductionController {

    private final TeamMemberIntroductionService teamMemberIntroductionService;

    // 팀원 소개
    @PostMapping("/team/member")
    @MemberOnly
    public ResponseEntity<?> createTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        log.info("memberId={}의 팀원 소개 생성 요청이 들어왔습니다.", accessor.getMemberId());
        if (teamMemberIntroductionCreateRequest.getTeamMemberIntroductionText().isEmpty() ||
                teamMemberIntroductionCreateRequest.getTeamMemberName().isEmpty() ||
                teamMemberIntroductionCreateRequest.getTeamMemberRole().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_TEAM_MEMBER_INTRODUCTION);
        }
        final Long teamMemberIntroductionId = teamMemberIntroductionService.saveTeamMemberIntroduction(accessor.getMemberId(), teamMemberIntroductionCreateRequest);
        return ResponseEntity.status(CREATED).body(teamMemberIntroductionId);
    }

    // 수정
    @PostMapping("/team/member/{teamMemberIntroductionId}")
    @MemberOnly
    public ResponseEntity<?> updateTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberIntroductionId,
            @RequestBody @Valid TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        log.info("memberId={}의 teamMemberIntroductionId={} 수정 요청이 발생하였습니다.", accessor.getMemberId(), teamMemberIntroductionId);
        teamMemberIntroductionService.validateTeamMemberIntroductionByMember(accessor.getMemberId());
        final Long savedTeamMemberIntroductionId = teamMemberIntroductionService.updateTeamMemberIntroduction(teamMemberIntroductionId, teamMemberIntroductionCreateRequest);
        return ResponseEntity.ok().body(savedTeamMemberIntroductionId);
    }

    @PostMapping("/team/members")
    @MemberOnly
    public ResponseEntity<Void> createTeamMemberIntroductions(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequests
    ) {
        teamMemberIntroductionService.saveTeamMemberIntroductions(accessor.getMemberId(), teamMemberIntroductionCreateRequests);
        return ResponseEntity.status(CREATED).build();
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

