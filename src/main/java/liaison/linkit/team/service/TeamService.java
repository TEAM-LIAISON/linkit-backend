package liaison.linkit.team.service;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.team.business.TeamCurrentStateMapper;
import liaison.linkit.team.business.TeamMapper;
import liaison.linkit.team.business.TeamMemberMapper;
import liaison.linkit.team.business.TeamScaleMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamCurrentState;
import liaison.linkit.team.domain.TeamMember;
import liaison.linkit.team.domain.TeamRegion;
import liaison.linkit.team.domain.scale.Scale;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.state.TeamState;
import liaison.linkit.team.implement.TeamCommandAdapter;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.region.TeamRegionCommandAdapter;
import liaison.linkit.team.implement.scale.ScaleQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleCommandAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.state.TeamCurrentStateCommandAdapter;
import liaison.linkit.team.implement.state.TeamStateQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamBasicInformRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final MemberQueryAdapter memberQueryAdapter;

    private final TeamMapper teamMapper;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamCommandAdapter teamCommandAdapter;

    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberCommandAdapter teamMemberCommandAdapter;

    private final ScaleQueryAdapter scaleQueryAdapter;

    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleCommandAdapter teamScaleCommandAdapter;
    private final TeamScaleMapper teamScaleMapper;

    private final TeamStateQueryAdapter teamStateQueryAdapter;
    private final TeamCurrentStateCommandAdapter teamCurrentStateCommandAdapter
    private final TeamCurrentStateMapper teamCurrentStateMapper;

    private final TeamRegionCommandAdapter teamRegionCommandAdapter;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;

    private final ImageValidator imageValidator;
    private final S3Uploader s3Uploader;


    public TeamResponseDTO.AddTeamResponse createTeam(
            final Long memberId,
            final MultipartFile teamLogoImage,
            final AddTeamBasicInformRequest addTeamBasicInformRequest
    ) {
        String teamLogoImagePath = null;

        // 회원 조회
        final Member member = memberQueryAdapter.findById(memberId);

        // 사용자가 첨부한 이미지의 유효성 판단 이후에 이미지 업로드 진행
        if (imageValidator.validatingImageUpload(teamLogoImage)) {
            teamLogoImagePath = s3Uploader.uploadTeamBasicLogoImage(new ImageFile(teamLogoImage));
        }

        // 팀 생성
        final Team team = teamMapper.toTeam(teamLogoImagePath, addTeamBasicInformRequest);
        final Team savedTeam = teamCommandAdapter.add(team);

        // 팀원에 추가
        final TeamMember teamMember = teamMemberMapper.toTeamMember(member, savedTeam);
        teamMemberCommandAdapter.addTeamMember(teamMember);

        // 팀 규모 저장
        final Scale scale = scaleQueryAdapter.findByScaleName(addTeamBasicInformRequest.getScaleName());
        final TeamScale teamScale = new TeamScale(null, savedTeam, scale);
        teamScaleCommandAdapter.save(teamScale);
        final TeamScaleItem teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);

        // 팀 지역 저장
        final Region region = regionQueryAdapter.findByCityNameAndDivisionName(addTeamBasicInformRequest.getCityName(), addTeamBasicInformRequest.getDivisionName());
        final TeamRegion teamRegion = new TeamRegion(null, savedTeam, region);
        teamRegionCommandAdapter.save(teamRegion);
        final RegionDetail regionDetail = regionMapper.toRegionDetail(region);

        // 팀 현재 상태 저장
        List<String> teamStateNames = addTeamBasicInformRequest.getTeamStateNames();
        List<TeamCurrentState> teamCurrentStates = new ArrayList<>();

        for (String teamStateName : teamStateNames) {
            // ProfileState 엔티티 조회
            TeamState teamState = teamStateQueryAdapter.findByStateName(teamStateName);

            // ProfileCurrentState 엔티티 생성
            TeamCurrentState teamCurrentState = new TeamCurrentState(null, savedTeam, teamState);
            teamCurrentStates.add(teamCurrentState);
        }

        teamCurrentStateCommandAdapter.saveAll(teamCurrentStates);
        List<TeamCurrentStateItem> teamCurrentStateItems = teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);

        // 생성된 팀의 정보를 반환
        return teamMapper.toAddTeam(savedTeam, teamScaleItem, regionDetail, teamCurrentStateItems);
    }

    public TeamResponseDTO.SaveTeamBasicInformResponse saveTeamBasicInform(
            final Long memberId,
            final Long teamId,
            final MultipartFile teamLogoImage,
            final AddTeamBasicInformRequest addTeamBasicInformRequest
    ) {
        String teamLogoImagePath = null;

        // 팀 조회
        final Team team = teamQueryAdapter.findById(teamId);

        // 지역 조회
        final Region region = regionQueryAdapter.findByCityNameAndDivisionName(addTeamBasicInformRequest.getCityName(), addTeamBasicInformRequest.getDivisionName());

        team.setTeamName(addTeamBasicInformRequest.getTeamName());
        team.setTeamShortDescription(addTeamBasicInformRequest.getTeamShortDescription());

        // 사용자가 새로운 이미지를 업로드
        if (!teamLogoImage.isEmpty() && imageValidator.validatingImageUpload(teamLogoImage)) {
            // 이전에 업로드한 팀 로고 이미지가 존재
            if (team.getTeamLogoImagePath() != null) {
                s3Uploader.deleteS3Image(team.getTeamLogoImagePath());
            }
            teamLogoImagePath = s3Uploader.uploadTeamBasicLogoImage(new ImageFile(teamLogoImage));
        }
        team.setTeamLogoImagePath(teamLogoImagePath);

        return teamMapper.toSaveTeam(team);
    }

    // 로그인한 사용자가 팀을 상세 조회한 케이스
    public TeamResponseDTO.TeamDetail getLoggedInTeamDetail(final Long memberId, final String teamName) {
        final Team targetTeam = teamQueryAdapter.findByTeamName(teamName);

        // 6. 멤버 여부 확인
        boolean isMyTeam = teamMemberQueryAdapter.isMemberOfTeam(targetTeam.getId(), memberId);

        final List<TeamCurrentState> teamCurrentStates = teamQueryAdapter.findTeamCurrentStatesByTeamId(targetTeam.getId());
        final List<TeamCurrentStateItem> teamCurrentStateItems = teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);
        log.info("팀 상태 정보 조회 성공");

        TeamScaleItem teamScaleItem = null;
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(targetTeam.getId())) {
            final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(targetTeam.getId());
            teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
        }

        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsTeamRegionByTeamId((targetTeam.getId()))) {
            final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(targetTeam.getId());
            regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
        }
        log.info("팀 지역 정보 조회 성공");

        final TeamInformMenu teamInformMenu = teamMapper.toTeamInformMenu(targetTeam, teamCurrentStateItems, teamScaleItem, regionDetail);

        return teamMapper.toTeamDetail(isMyTeam, teamInformMenu);
    }

    // 로그인하지 않은 사용자가 팀을 상세 조회한 케이스
    public TeamResponseDTO.TeamDetail getLoggedOutTeamDetail(final String teamName) {
        final Team targetTeam = teamQueryAdapter.findByTeamName(teamName);

        final List<TeamCurrentState> teamCurrentStates = teamQueryAdapter.findTeamCurrentStatesByTeamId(targetTeam.getId());
        final List<TeamCurrentStateItem> teamCurrentStateItems = teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);
        log.info("팀 상태 정보 조회 성공");

        TeamScaleItem teamScaleItem = null;
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(targetTeam.getId())) {
            final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(targetTeam.getId());
            teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
        }

        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsTeamRegionByTeamId((targetTeam.getId()))) {
            final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(targetTeam.getId());
            regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
        }
        log.info("팀 지역 정보 조회 성공");

        final TeamInformMenu teamInformMenu = teamMapper.toTeamInformMenu(targetTeam, teamCurrentStateItems, teamScaleItem, regionDetail);

        return teamMapper.toTeamDetail(false, teamInformMenu);
    }
}
