package liaison.linkit.profile.presentation.log;

import java.util.Optional;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileLogCommentService;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
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
public class ProfileLogCommentController {

    private final ProfileLogCommentService profileLogCommentService;

    /**
     * 프로필 로그에 댓글 작성 API
     *
     * @param accessor 인증된 사용자 정보
     * @param profileLogId 댓글을 작성할 프로필 로그 ID
     * @param request 댓글 작성 요청 데이터
     * @return 댓글 작성 결과 응답
     */
    @PostMapping("/profile/log/{profileLogId}/comment")
    @MemberOnly
    @Logging(item = "Profile_Log_Comment", action = "Create", includeResult = true)
    public CommonResponse<ProfileLogCommentResponseDTO.AddProfileLogCommentResponse>
            addProfileLogComment(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileLogId,
                    @Valid @RequestBody
                            final ProfileLogCommentRequestDTO.AddProfileLogCommentRequest request) {

        return CommonResponse.onSuccess(
                profileLogCommentService.addProfileLogComment(
                        accessor.getMemberId(), profileLogId, request));
    }

    /**
     * 프로필 로그 댓글 수정 API
     *
     * @param profileLogCommentId 수정할 프로필 로그 댓글 ID
     */
    @PostMapping("/profile/log/comment/{profileLogCommentId}")
    @MemberOnly
    @Logging(item = "Profile_Log_Comment", action = "Update", includeResult = true)
    public CommonResponse<ProfileLogCommentResponseDTO.UpdateProfileLogCommentResponse>
            updateProfileLogComment(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileLogCommentId,
                    @Valid @RequestBody
                            ProfileLogCommentRequestDTO.UpdateProfileLogCommentRequest request) {

        return CommonResponse.onSuccess(
                profileLogCommentService.updateProfileLogComment(
                        accessor.getMemberId(), profileLogCommentId, request));
    }

    /**
     * 프로필 로그 댓글 조회 API
     *
     * @param profileLogId 조회할 프로필 로그 ID
     * @return 댓글 목록 조회 결과 응답
     */
    @GetMapping("/profile/log/{profileLogId}/comments")
    @Logging(item = "Profile_Log_Comment", action = "Read", includeResult = true)
    public CommonResponse<CursorResponse<ProfileLogCommentResponseDTO.ParentCommentResponse>>
            getPageProfileLogComments(
                    @Auth final Accessor accessor,
                    @PathVariable final Long profileLogId,
                    @RequestParam(value = "cursor", required = false) final String cursor,
                    @RequestParam(value = "size", defaultValue = "10") final int size) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                profileLogCommentService.getPageProfileLogComments(
                        optionalMemberId, profileLogId, cursor, size));
    }

    /**
     * 프로필 로그 댓글 삭제 API
     *
     * @param accessor 인증된 사용자 정보
     * @param profileLogCommentId 삭제할 프로필 로그 댓글 ID
     * @return 댓글 삭제 결과 응답
     */
    @PostMapping("/profile/log/comment/{profileLogCommentId}/delete")
    @MemberOnly
    @Logging(item = "Profile_Log_Comment", action = "Delete", includeResult = true)
    public CommonResponse<ProfileLogCommentResponseDTO.DeleteProfileLogCommentResponse>
            deleteProfileLogComment(
                    @Auth final Accessor accessor, @PathVariable final Long profileLogCommentId) {

        return CommonResponse.onSuccess(
                profileLogCommentService.deleteProfileLogComment(
                        accessor.getMemberId(), profileLogCommentId));
    }
}
