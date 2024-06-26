package liaison.linkit.team.dto.response.attach;

import liaison.linkit.team.domain.attach.TeamAttachFile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamAttachFileResponse {
    private Long id;
    private String teamAttachFileName;
    private String teamAttachFilePath;

    public static TeamAttachFileResponse getTeamAttachFile(
            final TeamAttachFile teamAttachFile
    ) {
        return new TeamAttachFileResponse(
                teamAttachFile.getId(),
                teamAttachFile.getTeamAttachFileName(),
                teamAttachFile.getTeamAttachFilePath()
        );
    }

}
