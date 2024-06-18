package liaison.linkit.profile.dto.response.Attach;

import liaison.linkit.profile.domain.Attach.AttachUrl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttachUrlResponse {
    private final String attachUrlName;
    private final String attachUrl;

    public static AttachUrlResponse personalAttachUrl(final AttachUrl attachUrl) {
        return new AttachUrlResponse(
                attachUrl.getAttachUrlName(),
                attachUrl.getAttachUrl()
        );
    }
}
