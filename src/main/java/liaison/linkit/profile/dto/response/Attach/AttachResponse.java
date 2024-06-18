package liaison.linkit.profile.dto.response.Attach;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AttachResponse {

    private List<AttachUrlResponse> attachUrlResponseList;
    private List<AttachFileResponse> attachFileResponseList;

    public AttachResponse() {
        this.attachUrlResponseList = null;
        this.attachFileResponseList = null;
    }

    public static AttachResponse getAttachResponse(
            final List<AttachUrlResponse> attachUrlResponses,
            final List<AttachFileResponse> attachFileResponses)
    {
        return new AttachResponse(
                attachUrlResponses,
                attachFileResponses
        );
    }
}
