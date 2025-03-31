package liaison.linkit.profile.presentation.log;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.profile.business.service.ProfileLogCommentService;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
