package liaison.linkit.profile.dto.response.Attach;

import liaison.linkit.profile.domain.Attach.AttachFile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachFileResponse {
    private String attachFile;

    public static AttachFileResponse personalAttachFile(
            final AttachFile attachFile
    ) {
        return new AttachFileResponse(
                attachFile.getAttachFile()
        );
    }
}
