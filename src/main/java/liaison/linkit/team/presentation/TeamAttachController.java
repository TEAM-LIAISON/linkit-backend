package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.attach.TeamAttachUrlCreateRequest;
import liaison.linkit.team.service.TeamAttachService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TeamAttachController {

    private final TeamAttachService teamAttachService;

    // 외부 링크 리스트 생성 요청
    @PostMapping("/team/attach/url")
    @MemberOnly
    public ResponseEntity<Void> createTeamAttachUrl(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamAttachUrlCreateRequest> teamAttachUrlCreateRequests
    ) {
        if (teamAttachUrlCreateRequests.isEmpty()) {
            log.info("teamAttachUrlCreateRequest가 비어있습니다.");
            teamAttachService.deleteAllTeamAttachUrl(accessor.getMemberId());
        } else{
            teamAttachService.saveUrl(accessor.getMemberId(), teamAttachUrlCreateRequests);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/team/attach/url/{teamAttachUrlId}")
    @MemberOnly
    public ResponseEntity<Void> deleteTeamAttachUrl(
            @Auth final Accessor accessor,
            @PathVariable final Long teamAttachUrlId
    ) {
        teamAttachService.validateTeamAttachUrlByMember(accessor.getMemberId());
        teamAttachService.deleteTeamAttachUrl(accessor.getMemberId(), teamAttachUrlId);
        return ResponseEntity.noContent().build();
    }

    // 외부 링크 파일 1개(단일) 생성 요청
//    @PostMapping("/file")
//    @MemberOnly
//    public ResponseEntity<Void> createTeamAttachFile(
//            @Auth final Accessor accessor,
//            @RequestPart MultipartFile teamAttachFile
//    ) {
//        teamAttachService.saveFile(accessor.getMemberId(), teamAttachFile);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
}
