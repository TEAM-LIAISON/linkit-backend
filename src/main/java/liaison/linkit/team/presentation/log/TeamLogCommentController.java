package liaison.linkit.team.presentation.log;

import java.util.Optional;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.business.service.log.TeamLogCommentService;
import liaison.linkit.team.presentation.log.dto.TeamLogCommentRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogCommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TeamLogCommentController {

    private final TeamLogCommentService teamLogCommentService;

    /**
     * 팀 로그에 댓글 작성 API
     *
     * @param accessor 인증된 사용자 정보
     * @param teamLogId 댓글을 작성할 팀 로그 ID
     * @param request 댓글 작성 요청 데이터
     * @return 댓글 작성 결과 응답
     */
    @PostMapping("/team/log/{teamLogId}/comment")
    @MemberOnly
    @Logging(item = "Team_Log_Comment", action = "Create", includeResult = true)
    public CommonResponse<TeamLogCommentResponseDTO.AddTeamLogCommentResponse> addProfileLogComment(
            @Auth final Accessor accessor,
            @PathVariable final Long teamLogId,
            @Valid @RequestBody final TeamLogCommentRequestDTO.AddTeamLogCommentRequest request) {

        return CommonResponse.onSuccess(
                teamLogCommentService.addTeamLogComment(
                        accessor.getMemberId(), teamLogId, request));
    }

    /**
     * 팀 로그 댓글 수정 API
     *
     * @param teamLogCommentId 수정할 프로필 로그 댓글 ID
     */
    @PostMapping("/team/log/comment/{teamLogCommentId}")
    @MemberOnly
    @Logging(item = "Team_Log_Comment", action = "Update", includeResult = true)
    public CommonResponse<TeamLogCommentResponseDTO.UpdateTeamLogCommentResponse>
            updateTeamLogComment(
                    @Auth final Accessor accessor,
                    @PathVariable final Long teamLogCommentId,
                    @Valid @RequestBody
                            TeamLogCommentRequestDTO.UpdateTeamLogCommentRequest request) {

        return CommonResponse.onSuccess(
                teamLogCommentService.updateTeamLogComment(
                        accessor.getMemberId(), teamLogCommentId, request));
    }

    /**
     * 팀 로그 댓글 조회 API
     *
     * @param teamLogId 조회할 프로필 로그 ID
     * @return 댓글 목록 조회 결과 응답
     */
    @GetMapping("/team/log/{teamLogId}/comments")
    @Logging(item = "Team_Log_Comment", action = "Read", includeResult = true)
    public CommonResponse<CursorResponse<TeamLogCommentResponseDTO.ParentCommentResponse>>
            getPageTeamLogComments(
                    @Auth final Accessor accessor,
                    @PathVariable final Long teamLogId,
                    @RequestParam(value = "cursor", required = false) final String cursor,
                    @RequestParam(value = "size", defaultValue = "10") final int size) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                teamLogCommentService.getPageTeamLogComments(
                        optionalMemberId, teamLogId, cursor, size));
    }

    /**
     * 팀 로그 댓글 삭제 API
     *
     * @param accessor 인증된 사용자 정보
     * @param teamLogCommentId 삭제할 팀 로그 댓글 ID
     * @return 댓글 삭제 결과 응답
     */
    @PostMapping("/team/log/comment/{teamLogCommentId}/delete")
    @MemberOnly
    @Logging(item = "Team_Log_Comment", action = "Delete", includeResult = true)
    public CommonResponse<TeamLogCommentResponseDTO.DeleteTeamLogCommentResponse>
            deleteTeamLogComment(
                    @Auth final Accessor accessor, @PathVariable final Long teamLogCommentId) {

        return CommonResponse.onSuccess(
                teamLogCommentService.deleteTeamLogComment(
                        accessor.getMemberId(), teamLogCommentId));
    }
}
