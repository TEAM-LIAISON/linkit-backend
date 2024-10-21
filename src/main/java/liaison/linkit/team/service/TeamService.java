package liaison.linkit.team.service;

import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.image.domain.ImageFile;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.team.business.TeamMapper;
import liaison.linkit.team.business.TeamMemberMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamMember;
import liaison.linkit.team.implement.TeamCommandAdapter;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final MemberQueryAdapter memberQueryAdapter;

    private final TeamMapper teamMapper;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamCommandAdapter teamCommandAdapter;

    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberCommandAdapter teamMemberCommandAdapter;

    private final RegionQueryAdapter regionQueryAdapter;

    private final ImageValidator imageValidator;
    private final S3Uploader s3Uploader;

    public TeamResponseDTO.AddTeamResponse createTeam(
            final Long memberId,
            final MultipartFile teamLogoImage,
            final AddTeamRequest addTeamRequest
    ) {
        String teamLogoImagePath = null;
        // 회원 조회
        final Member member = memberQueryAdapter.findById(memberId);

        // 사용자가 입력한 정보에서 지역 객체 조회
        final Region region = regionQueryAdapter.findByCityNameAndDivisionName(addTeamRequest.getCityName(), addTeamRequest.getDivisionName());

        // 사용자가 첨부한 이미지의 유효성 판단 이후에 이미지 업로드 진행
        if (imageValidator.validatingImageUpload(teamLogoImage)) {
            teamLogoImagePath = s3Uploader.uploadTeamBasicLogoImage(new ImageFile(teamLogoImage));
        }

        // 팀 생성
        final Team team = teamMapper.toTeam(teamLogoImagePath, addTeamRequest, region);
        final Team savedTeam = teamCommandAdapter.add(team);

        // 팀원에 추가
        final TeamMember teamMember = teamMemberMapper.toTeamMember(member, savedTeam);
        teamMemberCommandAdapter.add(teamMember);

        // 생성된 팀의 정보를 반환한다.
        return teamMapper.toAddTeam(savedTeam);
    }
}
