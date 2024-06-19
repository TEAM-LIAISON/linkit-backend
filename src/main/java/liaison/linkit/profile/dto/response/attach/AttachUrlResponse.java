package liaison.linkit.profile.dto.response.attach;

import liaison.linkit.profile.domain.attach.AttachUrl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttachUrlResponse {
    private final Long id;
    private final String attachUrlName;
    private final String attachUrl;

    public static AttachUrlResponse personalAttachUrl(final AttachUrl attachUrl) {
        return new AttachUrlResponse(
                attachUrl.getId(),
                attachUrl.getAttachUrlName(),
                attachUrl.getAttachUrl()
        );
    }
}
