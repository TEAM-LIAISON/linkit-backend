package liaison.linkit.profile.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO;

@Mapper
public class ProfileLogCommentMapper {

    public ProfileLogCommentResponseDTO.AddProfileLogCommentResponse toAddProfileLogCommentResponse(
            final ProfileLogComment profileLogComment,
            final Profile authorProfile,
            final Long profileLogId) {
        return ProfileLogCommentResponseDTO.AddProfileLogCommentResponse.builder()
                .commentId(profileLogComment.getId())
                .profileLogId(profileLogId)
                .authorProfileId(authorProfile.getId())
                .authorName(authorProfile.getMember().getMemberBasicInform().getMemberName())
                .authorProfileImagePath(authorProfile.getProfileImagePath())
                .emailId(authorProfile.getMember().getEmailId())
                .content(profileLogComment.getContent())
                .createdAt(DateUtils.formatRelativeTime(profileLogComment.getCreatedAt()))
                .isParentComment(profileLogComment.getParentComment() == null)
                .parentCommentId(
                        profileLogComment.getParentComment() != null
                                ? profileLogComment.getParentComment().getId()
                                : null)
                .build();
    }
}
