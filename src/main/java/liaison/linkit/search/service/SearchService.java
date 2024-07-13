package liaison.linkit.search.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SearchService {

    private final MemberRepository memberRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final MiniProfileKeywordRepository miniProfileKeywordRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;
    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;


    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }


    @Transactional(readOnly = true)
    public Page<MiniProfileResponse> findPrivateMiniProfile(
            final Pageable pageable,
            final String teamBuildingFieldName,
            final String jobRoleName,
            final String skillName,
            final String cityName,
            String divisionName
    ) {

        if ("전체".equals(divisionName)) {
            divisionName = null;
        }

        final Page<MiniProfile> miniProfiles = miniProfileRepository.findAllByOrderByCreatedDateDesc(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                pageable
        );

        log.info("miniProfiles={}", miniProfiles);
        return miniProfiles.map(this::convertToMiniProfileResponse);
    }

    @Transactional(readOnly = true)
    public Page<TeamMiniProfileResponse> findTeamMiniProfile(
            final Pageable pageable,
            final String teamBuildingFieldName,
            final String jobRoleName,
            final String skillName,
            final String cityName,
            String divisionName,
            final String activityTagName
    ) {
        Page<TeamMiniProfile> teamMiniProfiles = teamMiniProfileRepository.findAllByOrderByCreatedDateDesc(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                activityTagName,
                pageable
        );
        return teamMiniProfiles.map(this::convertToTeamMiniProfileResponse);
    }

    private TeamMiniProfileResponse convertToTeamMiniProfileResponse(final TeamMiniProfile teamMiniProfile) {
        List<String> teamKeywordNames = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId()).stream()
                .map(TeamMiniProfileKeyword::getTeamKeywordNames)
                .toList();

        return new TeamMiniProfileResponse(
                teamMiniProfile.getId(),
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType(),
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getTeamProfileTitle(),
                teamMiniProfile.getIsTeamActivate(),
                teamMiniProfile.getTeamLogoImageUrl(),
                teamKeywordNames
        );
    }

    private MiniProfileResponse convertToMiniProfileResponse(final MiniProfile miniProfile) {
        final String memberName = getMemberNameByMiniProfile(miniProfile.getId());

        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .collect(Collectors.toList());

        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(miniProfile.getProfile().getId());
        final List<JobRole> jobRoleList = profileJobRoleList.stream()
                .map(ProfileJobRole::getJobRole)
                .toList();

        final List<String> jobRoleNames = jobRoleList.stream()
                .map(JobRole::getJobRoleName)
                .toList();

        return new MiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getMiniProfileImg(),
                miniProfile.isActivate(),
                myKeywordNames,
                memberName,
                jobRoleNames
        );
    }

    private List<ProfileJobRole> getProfileJobRoleList(final Long profileId) {
        return profileJobRoleRepository.findAllByProfileId(profileId);
    }

    @Transactional(readOnly = true)
    public String getMemberNameByMiniProfile(final Long miniProfileId) {
        final Long profileId = getProfileIdByMiniProfile(miniProfileId);
        final Member member = getMember(profileId);
        final MemberBasicInform memberBasicInform = getMemberBasicInform(member.getId());
        return memberBasicInform.getMemberName();
    }

    private Long getProfileIdByMiniProfile(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        return miniProfile.getProfile().getId();
    }

    // 회원 기본 정보를 가져오는 메서드
    private MemberBasicInform getMemberBasicInform(final Long memberId) {


        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
    }
}
