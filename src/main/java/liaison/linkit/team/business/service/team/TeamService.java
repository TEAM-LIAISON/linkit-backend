package liaison.linkit.team.business.service.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.global.DeleteUtil;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.HeaderNotificationService;
import liaison.linkit.notification.service.NotificationService;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import liaison.linkit.search.presentation.dto.team.FlatTeamDTO;
import liaison.linkit.team.business.assembler.team.TeamDetailAssembler;
import liaison.linkit.team.business.assembler.team.TeamInformMenuAssembler;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.business.mapper.state.TeamCurrentStateMapper;
import liaison.linkit.team.business.mapper.team.TeamMapper;
import liaison.linkit.team.business.mapper.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.repository.team.TeamRepository;
import liaison.linkit.team.domain.scale.Scale;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.state.TeamCurrentState;
import liaison.linkit.team.domain.state.TeamState;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.team.type.TeamStatus;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
import liaison.linkit.team.exception.team.DeleteTeamBadRequestException;
import liaison.linkit.team.exception.team.DuplicateTeamCodeException;
import liaison.linkit.team.implement.region.TeamRegionCommandAdapter;
import liaison.linkit.team.implement.region.TeamRegionQueryAdapter;
import liaison.linkit.team.implement.scale.ScaleQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleCommandAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.state.TeamCurrentStateCommandAdapter;
import liaison.linkit.team.implement.state.TeamCurrentStateQueryAdapter;
import liaison.linkit.team.implement.state.TeamStateQueryAdapter;
import liaison.linkit.team.implement.team.TeamCommandAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamRequest;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.UpdateTeamRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.UpdateTeamResponse;
import liaison.linkit.visit.event.TeamVisitedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final TeamCurrentStateQueryAdapter teamCurrentStateQueryAdapter;
    private final TeamCurrentStateCommandAdapter teamCurrentStateCommandAdapter;
    private final TeamCurrentStateMapper teamCurrentStateMapper;

    private final TeamRegionCommandAdapter teamRegionCommandAdapter;
    private final TeamRegionQueryAdapter teamRegionQueryAdapter;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;

    private final ImageValidator imageValidator;
    private final S3Uploader s3Uploader;
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final HeaderNotificationService headerNotificationService;
    private final DeleteUtil deleteUtil;
    private final TeamInformMenuAssembler teamInformMenuAssembler;
    private final TeamDetailAssembler teamDetailAssembler;

    private final ProfileQueryAdapter profileQueryAdapter;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TeamRepository teamRepository;
    private final TeamScrapRepository teamScrapRepository;

    // 초기 팀 생성
    public TeamResponseDTO.AddTeamResponse createTeam(
            final Long memberId,
            final MultipartFile teamLogoImage,
            final AddTeamRequest addTeamRequest) {
        // 회원 조회
        final Member member = memberQueryAdapter.findById(memberId);

        if (teamQueryAdapter.existsByTeamCode(addTeamRequest.getTeamCode())) {
            throw DuplicateTeamCodeException.EXCEPTION;
        }

        // 팀 생성
        final Team team = teamMapper.toTeam(addTeamRequest);
        final Team savedTeam = teamCommandAdapter.add(team);
        // 사용자가 새로운 이미지를 업로드
        String teamLogoImagePath = null;
        if (imageValidator.validatingImageUpload(teamLogoImage)) {
            // 팀 로고 이미지 업로드
            teamLogoImagePath = s3Uploader.uploadTeamLogoImage(new ImageFile(teamLogoImage));

            // 새로운 로고 이미지 저장
            savedTeam.setTeamLogoImagePath(teamLogoImagePath);
        }

        // 팀원에 추가
        final TeamMember teamMember =
                teamMemberMapper.toTeamMember(member, savedTeam, TeamMemberType.TEAM_OWNER);
        teamMemberCommandAdapter.addTeamMember(teamMember);

        // 팀 규모 저장
        final Scale scale = scaleQueryAdapter.findByScaleName(addTeamRequest.getScaleName());
        final TeamScale teamScale = new TeamScale(null, savedTeam, scale);
        teamScaleCommandAdapter.save(teamScale);
        final TeamScaleItem teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);

        // 팀 지역 저장
        final Region region =
                regionQueryAdapter.findByCityNameAndDivisionName(
                        addTeamRequest.getCityName(), addTeamRequest.getDivisionName());
        final TeamRegion teamRegion = new TeamRegion(null, savedTeam, region);
        teamRegionCommandAdapter.save(teamRegion);
        final RegionDetail regionDetail = regionMapper.toRegionDetail(region);

        // 팀 현재 상태 저장
        List<String> teamStateNames = addTeamRequest.getTeamStateNames();
        List<TeamCurrentState> teamCurrentStates = new ArrayList<>();

        for (String teamStateName : teamStateNames) {

            TeamState teamState = teamStateQueryAdapter.findByStateName(teamStateName);

            // ProfileCurrentState 엔티티 생성
            TeamCurrentState teamCurrentState = new TeamCurrentState(null, savedTeam, teamState);
            teamCurrentStates.add(teamCurrentState);
        }

        teamCurrentStateCommandAdapter.saveAll(teamCurrentStates);
        List<TeamCurrentStateItem> teamCurrentStateItems =
                teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);

        // 생성된 팀의 정보를 반환
        return teamMapper.toAddTeam(savedTeam, teamScaleItem, regionDetail, teamCurrentStateItems);
    }

    public UpdateTeamResponse updateTeam(
            final Long memberId,
            final String teamCode,
            final MultipartFile teamLogoImage,
            final UpdateTeamRequest updateTeamRequest) {
        String teamLogoImagePath = null;

        // 팀 조회
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);

        // 팀 이름 업데이트
        team.updateTeam(
                updateTeamRequest.getTeamName(),
                updateTeamRequest.getTeamCode(),
                updateTeamRequest.getTeamShortDescription(),
                updateTeamRequest.getIsTeamPublic());
        // 팀 로고 이미지 처리
        try {
            // 팀 로고 이미지 처리
            if (teamLogoImage != null && !teamLogoImage.isEmpty()) {
                if (imageValidator.validatingImageUpload(teamLogoImage)) {
                    // 이전에 업로드한 팀 로고 이미지가 존재
                    if (team.getTeamLogoImagePath() != null) {
                        s3Uploader.deleteS3Image(team.getTeamLogoImagePath());
                    }

                    // 팀 로고 이미지 업로드
                    teamLogoImagePath =
                            s3Uploader.uploadTeamLogoImage(new ImageFile(teamLogoImage));

                    // 새로운 로고 이미지 저장
                    team.setTeamLogoImagePath(teamLogoImagePath);
                }
            }
        } catch (Exception e) {
            log.error("팀 로고 이미지 처리 중 오류 발생", e);
            // 필요에 따라 예외를 다시 던지거나 처리
            throw e;
        }

        // 팀 규모 처리
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
            teamScaleCommandAdapter.deleteAllByTeamId(team.getId());
        }

        final Scale scale = scaleQueryAdapter.findByScaleName(updateTeamRequest.getScaleName());
        final TeamScale teamScale = new TeamScale(null, team, scale);
        teamScaleCommandAdapter.save(teamScale);
        final TeamScaleItem teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);

        // 팀 지역 처리
        if (teamRegionQueryAdapter.existsTeamRegionByTeamId(team.getId())) {
            teamRegionCommandAdapter.deleteAllByTeamId(team.getId());
        }

        final Region region =
                regionQueryAdapter.findByCityNameAndDivisionName(
                        updateTeamRequest.getCityName(), updateTeamRequest.getDivisionName());
        final TeamRegion teamRegion = new TeamRegion(null, team, region);
        teamRegionCommandAdapter.save(teamRegion);
        final RegionDetail regionDetail = regionMapper.toRegionDetail(region);

        // 팀 현재 상태 처리
        if (teamCurrentStateQueryAdapter.existsTeamCurrentStatesByTeamId(team.getId())) {
            teamCurrentStateCommandAdapter.deleteAllByTeamId(team.getId());
        }

        List<String> teamStateNames = updateTeamRequest.getTeamStateNames();
        List<TeamCurrentState> teamCurrentStates = new ArrayList<>();
        for (String teamStateName : teamStateNames) {
            // ProfileState 엔티티 조회
            TeamState teamState = teamStateQueryAdapter.findByStateName(teamStateName);

            // ProfileCurrentState 엔티티 생성
            TeamCurrentState teamCurrentState = new TeamCurrentState(null, team, teamState);
            teamCurrentStates.add(teamCurrentState);
        }

        teamCurrentStateCommandAdapter.saveAll(teamCurrentStates);
        List<TeamCurrentStateItem> teamCurrentStateItems =
                teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);

        return teamMapper.toUpdateTeam(team, teamScaleItem, regionDetail, teamCurrentStateItems);
    }

    // 사용자가 팀을 상세 조회한 케이스
    public TeamResponseDTO.TeamDetail getTeamDetail(
            final Optional<Long> optionalMemberId, final String teamCode) {
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);
        final TeamInformMenu teamInformMenu =
                teamInformMenuAssembler.assembleTeamInformMenu(targetTeam, optionalMemberId);

        // 로그인한 사용자의 경우 방문자 저장 이벤트 실행
        if (optionalMemberId.isPresent()) {

            if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(
                    targetTeam.getId(), optionalMemberId.get())) {
                final Profile visitorProfile =
                        profileQueryAdapter.findByMemberId(optionalMemberId.get());

                applicationEventPublisher.publishEvent(
                        new TeamVisitedEvent(
                                targetTeam.getId(),
                                visitorProfile.getId(),
                                optionalMemberId,
                                "teamVisit"));
            }
        }

        return teamDetailAssembler.assembleTeamDetail(optionalMemberId, teamCode, teamInformMenu);
    }

    public TeamResponseDTO.TeamItems getTeamItems(final Long memberId) {
        final List<Team> teams = teamMemberQueryAdapter.getAllTeamsByMemberId(memberId);

        final List<TeamInformMenu> teamInformMenus = new ArrayList<>();

        for (Team team : teams) {
            TeamScaleItem teamScaleItem = null;
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
                final TeamScale teamScale =
                        teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
                teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }

            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
                final TeamRegion teamRegion =
                        regionQueryAdapter.findTeamRegionByTeamId(team.getId());
                regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
            }

            TeamInformMenu teamInformMenu =
                    teamMapper.toTeamInformMenu(team, false, 0, null, teamScaleItem, regionDetail);

            teamInformMenus.add(teamInformMenu);
        }

        return teamMapper.toTeamItems(teamInformMenus);
    }

    // 팀 삭제 요청
    public TeamResponseDTO.DeleteTeamResponse deleteTeam(
            final Long memberId, final String teamCode) {
        boolean isTeamLastDeleteRequester = false;

        final Member member = memberQueryAdapter.findById(memberId);
        final Team targetTeam = teamQueryAdapter.findByTeamCode(teamCode);

        if (!teamMemberQueryAdapter.isOwnerOrManagerOfTeam(targetTeam.getId(), memberId)) {
            throw DeleteTeamBadRequestException.EXCEPTION;
        }

        final TeamMember teamMember =
                teamMemberQueryAdapter.getTeamMemberByTeamCodeAndEmailId(
                        targetTeam.getTeamCode(), member.getEmailId());

        // 해당 회원이 삭제 요청을 보낸 것으로 수정
        teamMember.setTeamMemberManagingTeamState(TeamMemberManagingTeamState.REQUEST_DELETE);

        // 오너를 제외하고 등록된 팀원이 존재하는 경우
        if (teamMemberQueryAdapter.existsTeamMembersExceptOwnerByTeamCode(teamCode)) {
            // 팀원의 삭제 수락 요청이 모두 처리되어야 팀 삭제가 진행될 수 있다.
            teamCommandAdapter.updateTeamStatus(TeamStatus.DELETE_PENDING, teamCode);

            final List<TeamMember> teamMembers =
                    teamMemberQueryAdapter.getAllTeamManagers(targetTeam);

            for (TeamMember currentTeamMember : teamMembers) {
                // 팀원들에게 요청 알림 발송 (수정 필요)
                // (상대방님의 뫄뫄팀 삭제 요청)
                NotificationDetails removeTeamNotificationDetails =
                        NotificationDetails.removeTeamRequested(
                                teamCode,
                                targetTeam.getTeamLogoImagePath(),
                                member.getMemberBasicInform().getMemberName(),
                                targetTeam.getTeamName(),
                                false);

                notificationService.alertNewNotification(
                        notificationMapper.toNotification(
                                currentTeamMember.getMember().getId(),
                                NotificationType.TEAM,
                                SubNotificationType.REMOVE_TEAM_REQUESTED,
                                removeTeamNotificationDetails));
            }
        } else { // 오너만 해당 팀을 소유하고 있는 경우
            isTeamLastDeleteRequester = true;
            deleteUtil.deleteTeam(teamCode);

            NotificationDetails removeTeamNotificationDetails =
                    NotificationDetails.removeTeamCompleted(
                            teamCode,
                            targetTeam.getTeamLogoImagePath(),
                            targetTeam.getTeamName(),
                            false);

            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            memberId,
                            NotificationType.TEAM,
                            SubNotificationType.REMOVE_TEAM_COMPLETED,
                            removeTeamNotificationDetails));

            //            headerNotificationService.publishNotificationCount(memberId);
        }

        return teamMapper.toDeleteTeam(teamCode, isTeamLastDeleteRequester);
    }

    public TeamResponseDTO.TeamInformMenus getHomeTeamInformMenus(Optional<Long> optionalMemberId) {
        List<FlatTeamDTO> raw = teamRepository.findHomeTopTeams(4);
        List<TeamInformMenu> homeTeamInformMenus = getTeamInformMenus(4, optionalMemberId, raw);

        return teamMapper.toTeamInformMenus(homeTeamInformMenus);
    }

    @NotNull
    private List<TeamInformMenu> getTeamInformMenus(
            int size, Optional<Long> optionalMemberId, List<FlatTeamDTO> raw) {
        List<Long> teamIds = raw.stream().map(FlatTeamDTO::getTeamId).distinct().toList();
        Set<Long> scraps =
                optionalMemberId
                        .map(
                                memberId ->
                                        teamScrapRepository.findScrappedTeamIdsByMember(
                                                memberId, teamIds))
                        .orElse(Set.of());

        Map<Long, Integer> scrapCounts = teamScrapRepository.countScrapsGroupedByTeam(teamIds);

        List<TeamInformMenu> menus =
                teamInformMenuAssembler.assembleTeamInformMenus(raw, scraps, scrapCounts);

        return menus.size() > size ? menus.subList(0, size) : menus;
    }
}
