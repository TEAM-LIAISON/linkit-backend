package liaison.linkit.matching.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.matching.domain.PrivateMatching;
import liaison.linkit.matching.domain.TeamMatching;
import liaison.linkit.matching.domain.repository.PrivateMatchingRepository;
import liaison.linkit.matching.domain.repository.TeamMatchingRepository;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.dto.request.AllowMatchingRequest;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.dto.response.ReceivedMatchingResponse;
import liaison.linkit.matching.dto.response.RequestMatchingResponse;
import liaison.linkit.matching.dto.response.SuccessMatchingResponse;
import liaison.linkit.matching.dto.response.contact.SuccessContactResponse;
import liaison.linkit.matching.dto.response.existence.ExistenceProfileResponse;
import liaison.linkit.matching.dto.response.messageResponse.ReceivedPrivateMatchingMessageResponse;
import liaison.linkit.matching.dto.response.messageResponse.ReceivedTeamMatchingMessageResponse;
import liaison.linkit.matching.dto.response.messageResponse.RequestPrivateMatchingMessageResponse;
import liaison.linkit.matching.dto.response.messageResponse.RequestTeamMatchingMessageResponse;
import liaison.linkit.matching.dto.response.requestPrivateMatching.MyPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.requestTeamMatching.MyTeamMatchingResponse;
import liaison.linkit.matching.dto.response.toPrivateMatching.ToPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.ToTeamMatchingResponse;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementJobRole;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementJobRoleRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static liaison.linkit.global.exception.ExceptionCode.*;
import static liaison.linkit.matching.domain.type.MatchingStatus.REQUESTED;
import static liaison.linkit.matching.domain.type.MatchingType.PROFILE;
import static liaison.linkit.matching.domain.type.MatchingType.TEAM_PROFILE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchingService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final PrivateMatchingRepository privateMatchingRepository;
    private final TeamMatchingRepository teamMatchingRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;

    private final TeamProfileRepository teamProfileRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    private final TeamMemberAnnouncementJobRoleRepository teamMemberAnnouncementJobRoleRepository;

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    // 내 이력서 ID -> profile 객체 조회
    private Profile getProfileById(final Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));
    }
    // 팀 소개서 ID -> team Profile 객체 조회
    private TeamProfile getTeamProfileById(final Long teamProfileId) {
        return teamProfileRepository.findById(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
    }
    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
    }

    private PrivateMatching getPrivateMatching(final Long privateMatchingId) {
        return privateMatchingRepository.findById(privateMatchingId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PRIVATE_MATCHING_BY_ID));
    }

    private TeamMatching getTeamMatching(final Long teamMatchingId) {
        return teamMatchingRepository.findById(teamMatchingId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MATCHING_BY_ID));
    }

    public void validateReceivedMatchingRequest(final Long memberId) {
        if(!privateMatchingRepository.existsByProfileId(getProfile(memberId).getId())){
            throw new AuthException(NOT_FOUND_PRIVATE_MATCHING_BY_ID);
        }
    }

    // 이미 애노테이션으로 매칭에 대한 권한을 체킹한 상태이다.
    // 내 이력서로 내 이력서에 발생한 매칭 요청을 저장한다.
    // 1번
    public void createPrivateProfileMatchingToPrivate(
            final Long memberId,
            // 해당 매칭 요청이 발생한 내 이력서에 대한 미니 프로필 -> 발생시키기
            final Long profileId,
            // 매칭 요청 생성
            final MatchingCreateRequest matchingCreateRequest
    ) {
        final Member member = getMember(memberId);
        log.info("memberId={}가 매칭 요청을 보냅니다.", memberId);

        final Profile profile = getProfileById(profileId);

        // 새로운 매칭 객체를 생성한다.
        final PrivateMatching newPrivateMatching = new PrivateMatching(
                null,
                // 요청 보낸 회원
                member,
                // 내 이력서의 프로필 아이디를 저장한다.
                profile,
                SenderType.PRIVATE,
                // 내 이력서로 요청을 보냈음을 저장
                PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED
        );

        // to 내 이력서
        privateMatchingRepository.save(newPrivateMatching);
    }

    // 이미 애노테이션으로 매칭에 대한 권한을 체킹한 상태이다.
    // 팀 소개서로 내 이력서에 발생한 매칭 요청을 저장한다.
    // 2번
    public void createTeamProfileMatchingToPrivate(
            final Long memberId,
            final Long profileId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        final Member member = getMember(memberId);
        final Profile profile = getProfileById(profileId);
        final PrivateMatching newPrivateMatching = new PrivateMatching(
                null,
                // 요청 보낸 회원
                member,
                // 내 이력서의 프로필 아이디를 저장한다.
                profile,
                // 팀 소개서로 나한테 요청 보냄
                SenderType.TEAM,
                // 내 이력서로 요청을 보냈음을 저장
                TEAM_PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED
        );

        privateMatchingRepository.save(newPrivateMatching);
    }

    // 3번
    // 팀 소개서가 팀 소개서에 요청을 보낸 경우
    public void createTeamProfileMatchingToTeam(
            final Long memberId,
            final Long teamMemberAnnouncementId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        // 멤버 객체 조회
        final Member member = getMember(memberId);

        // 팀원 공고 객체 조회
        final TeamMemberAnnouncement teamMemberAnnouncement = getTeamMemberAnnouncement(teamMemberAnnouncementId);

        // 해당 팀원 공고 객체에 대한 팀 매칭 객체 생성
        final TeamMatching newTeamMatching = new TeamMatching(
                null,
                // 요청 보낸 회원
                member,
                // 팀 소개서를 저장한다.
                teamMemberAnnouncement,
                SenderType.TEAM,
                // 팀 소개서로 요청을 보냈음을 저장
                TEAM_PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED
        );

        teamMatchingRepository.save(newTeamMatching);
    }

    private TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementRepository.findById(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID));
    }


    // 4번
    // 내 이력서가 팀 소개서에 요청을 보낸 경우
    public void createPrivateProfileMatchingToTeam(
            final Long memberId,
            final Long teamMemberAnnouncementId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        // 매칭 요청 주체 객체 조회
        final Member member = getMember(memberId);

        // 팀원 공고 객체 조회
        final TeamMemberAnnouncement teamMemberAnnouncement = getTeamMemberAnnouncement(teamMemberAnnouncementId);

        final TeamMatching newTeamMatching = new TeamMatching(
                null,
                // 요청 보낸 회원
                member,
                // 팀 소개서를 저장한다.
                teamMemberAnnouncement,
                SenderType.PRIVATE,
                // 내 이력서로 요청을 보냈음을 저장
                TEAM_PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED
        );

        teamMatchingRepository.save(newTeamMatching);
    }

    // 내가 받은 매칭
    public List<ReceivedMatchingResponse> getReceivedMatching(
            final Long memberId
    ) {
        // 해당 memberId에게 발생한 모든 매칭 요청을 조회해야 함.
        List<ToPrivateMatchingResponse> toPrivateMatchingResponseList = Collections.emptyList();
        List<ToTeamMatchingResponse> toTeamMatchingResponseList = Collections.emptyList();

        if (profileRepository.existsByMemberId(memberId)) {
            final Profile profile = getProfile(memberId);
            final List<PrivateMatching> privateMatchingList = privateMatchingRepository.findByProfileId(profile.getId());

            toPrivateMatchingResponseList = ToPrivateMatchingResponse.toPrivateMatchingResponse(privateMatchingList);
        }

        if (teamProfileRepository.existsByMemberId(memberId)) {
            // 내가 등록한 나의 팀 소개서 중 팀원 공고들이 필요함
            final TeamProfile teamProfile = getTeamProfile(memberId);
            final List<TeamMemberAnnouncement> teamMemberAnnouncementList = teamMemberAnnouncementRepository.findAllByTeamProfileId(teamProfile.getId());
            final List<Long> teamMemberAnnouncementIds = teamMemberAnnouncementList.stream().map(TeamMemberAnnouncement::getId).toList();
            final List<TeamMatching> teamMatchingList = teamMatchingRepository.findAllByTeamMemberAnnouncementIds(teamMemberAnnouncementIds);
            toTeamMatchingResponseList = ToTeamMatchingResponse.toTeamMatchingResponse(teamMatchingList);
        }
        return ReceivedMatchingResponse.toReceivedMatchingResponse(toPrivateMatchingResponseList, toTeamMatchingResponseList);
    }

    // 내가 매칭 요청 보낸 것을 조회하는 메서드
    public List<RequestMatchingResponse> getMyRequestMatching(final Long memberId) {
        List<MyPrivateMatchingResponse> myPrivateMatchingResponseList = Collections.emptyList();
        List<MyTeamMatchingResponse> myTeamMatchingResponseList = Collections.emptyList();


        if (profileRepository.existsByMemberId(memberId)) {
            final List<PrivateMatching> privateMatchingList = privateMatchingRepository.findByMemberId(memberId);
            myPrivateMatchingResponseList = MyPrivateMatchingResponse.myPrivateMatchingResponseList(privateMatchingList);
        }

        if (teamProfileRepository.existsByMemberId(memberId)) {
            final List<TeamMatching> teamMatchingList = teamMatchingRepository.findByMemberId(memberId);
            myTeamMatchingResponseList = MyTeamMatchingResponse.myTeamMatchingResponses(teamMatchingList);
        }

        return RequestMatchingResponse.requestMatchingResponseList(myPrivateMatchingResponseList, myTeamMatchingResponseList);
    }

    // 성사된 매칭
    public List<SuccessMatchingResponse> getMySuccessMatching(final Long memberId) {
        List<ToPrivateMatchingResponse> toPrivateMatchingResponseList = Collections.emptyList();
        List<ToTeamMatchingResponse> toTeamMatchingResponseList = Collections.emptyList();
        List<MyPrivateMatchingResponse> myPrivateMatchingResponseList = Collections.emptyList();
        List<MyTeamMatchingResponse> myTeamMatchingResponseList = Collections.emptyList();

        if (profileRepository.existsByMemberId(memberId)) {
            // 나의 내 이력서로 받은 매칭 요청 조회
            final Profile profile = getProfile(memberId);
            final List<PrivateMatching> privateReceivedMatchingList = privateMatchingRepository.findSuccessReceivedMatching(profile.getId());
            toPrivateMatchingResponseList = ToPrivateMatchingResponse.toPrivateMatchingResponse(privateReceivedMatchingList);

            // 내가 보낸 매칭 요청 조회
            final List<PrivateMatching> privateRequestMatchingList = privateMatchingRepository.findSuccessRequestMatching(memberId);
            myPrivateMatchingResponseList = MyPrivateMatchingResponse.myPrivateMatchingResponseList(privateRequestMatchingList);
        }

        if (teamProfileRepository.existsByMemberId(memberId)) {
            // 나의 팀 소개서로 받은 매칭 요청 조회
            final TeamProfile teamProfile = getTeamProfile(memberId);
            final List<TeamMemberAnnouncement> teamMemberAnnouncementList = teamMemberAnnouncementRepository.findAllByTeamProfileId(teamProfile.getId());
            final List<Long> teamMemberAnnouncementIds = teamMemberAnnouncementList.stream().map(TeamMemberAnnouncement::getId).toList();
            final List<TeamMatching> teamReceivedMatchingList = teamMatchingRepository.findSuccessReceivedMatching(teamMemberAnnouncementIds);
            toTeamMatchingResponseList = ToTeamMatchingResponse.toTeamMatchingResponse(teamReceivedMatchingList);

            // 내가 팀 소개서로 보낸 매칭 요청 조회
            final List<TeamMatching> teamRequestMatchingList = teamMatchingRepository.findSuccessRequestMatching(memberId);
            myTeamMatchingResponseList = MyTeamMatchingResponse.myTeamMatchingResponses(teamRequestMatchingList);
        }

        return SuccessMatchingResponse.successMatchingResponseList(
                toPrivateMatchingResponseList,
                toTeamMatchingResponseList,
                myPrivateMatchingResponseList,
                myTeamMatchingResponseList
        );
    }

    // 나의 멤버 아이디
    public ExistenceProfileResponse getExistenceProfile(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 내 이력서, 팀 소개서 모두로 매칭 요청을 보낼 수 있는 상태
        if (profile.getCompletion() >= 80 && teamProfile.getTeamProfileCompletion() >= 80) {
            return new ExistenceProfileResponse(
                    true,
                    true
            );
        }
        else if (profile.getCompletion() >= 80 && teamProfile.getTeamProfileCompletion() < 80) {
            return new ExistenceProfileResponse(
                    true,
                    false
            );
        }
        else if (profile.getCompletion() < 80 && teamProfile.getTeamProfileCompletion() >= 80) {
            return new ExistenceProfileResponse(
                    false,
                    true
            );
        }
        else {
            return new ExistenceProfileResponse(
                    false,
                    false
            );
        }
    }

    // 내 이력서가 내 이력서에 보낸 경우
    public ReceivedPrivateMatchingMessageResponse getReceivedPrivateToPrivateMatchingMessage(
            // private Matching PK
            final Long privateMatchingId
    ) {
        // 이 private matching -> member -> 보낸 사람의 ID / profile -> 수신자의 내 이력서 ID
        final PrivateMatching privateMatching = getPrivateMatching(privateMatchingId);
        final List<String> jobRoleNames = getJobRoleNames(privateMatching.getMember().getId());

        return new ReceivedPrivateMatchingMessageResponse(
                privateMatching.getId(),
                privateMatching.getMember().getMemberBasicInform().getMemberName(),
                jobRoleNames,
                privateMatching.getRequestMessage(),
                false
        );
    }

    // 팀 소개서가 내 이력서에 보낸 경우
    public ReceivedPrivateMatchingMessageResponse getReceivedTeamToPrivateMatchingMessage(
            final Long privateMatchingId
    ) {
        // 보낸 팀의 정보가 필요
        final PrivateMatching privateMatching = getPrivateMatching(privateMatchingId);
        return new ReceivedPrivateMatchingMessageResponse(
                privateMatching.getId(),
                // 상대 팀의 이름 필요
                privateMatching.getMember().getTeamProfile().getTeamMiniProfile().getTeamName(),
                null,
                privateMatching.getRequestMessage(),
                false
        );
    }

    // 내 이력서가 팀 소개서에 보낸 경우
    public ReceivedTeamMatchingMessageResponse getReceivedPrivateToTeamMatchingMessage(
            final Long teamMatchingId
    ) {
        // 보낸 개인의 이름 필요
        final TeamMatching teamMatching = getTeamMatching(teamMatchingId);
        final List<String> jobRoleNames = getJobRoleNames(teamMatching.getMember().getId());
        return new ReceivedTeamMatchingMessageResponse(
                teamMatching.getId(),
                // 개인 이름
                teamMatching.getMember().getMemberBasicInform().getMemberName(),
                jobRoleNames,
                teamMatching.getRequestMessage(),
                false
        );
    }

    // 내가 보낸 매칭 요청에서 private_matching 조회하는 경우 (개인 - 개인인 경우)
    public RequestPrivateMatchingMessageResponse getRequestPrivateToPrivateMatchingMessage(
            final Long privateMatchingId
    ) {
        // 이 private matching -> member -> 나의 ID / profile -> 상대의 내 이력서 ID
        final PrivateMatching privateMatching = getPrivateMatching(privateMatchingId);

        // 상대의 jobRoleName 필요
        final List<String> jobRoleNames = getJobRoleNames(privateMatching.getProfile().getMember().getId());

        return new RequestPrivateMatchingMessageResponse(
                privateMatching.getId(),
                // 수신자 이름
                privateMatching.getProfile().getMember().getMemberBasicInform().getMemberName(),
                jobRoleNames,
                privateMatching.getRequestMessage(),
                privateMatching.getSenderType(),
                false
        );
    }

    // 내가 보낸 매칭 요청에서 private_matching 조회하는 경우 (팀 - 개인인 경우)
    public RequestPrivateMatchingMessageResponse getRequestTeamToPrivateMatchingMessage(
            final Long privateMatchingId
    ) {
        final PrivateMatching privateMatching = getPrivateMatching(privateMatchingId);
        // 상대의 jobRoleName 필요
        final List<String> jobRoleNames = getJobRoleNames(privateMatching.getProfile().getMember().getId());

        return new RequestPrivateMatchingMessageResponse(
                privateMatching.getId(),
                privateMatching.getProfile().getMember().getMemberBasicInform().getMemberName(),
                jobRoleNames,
                privateMatching.getRequestMessage(),
                privateMatching.getSenderType(),
                false
        );
    }

    // 내가 보낸 매칭 요청에서 team_matching 조회하는 경우 (개인 - 팀인 경우)
    public RequestTeamMatchingMessageResponse getRequestPrivateToTeamMatchingMessage(
            final Long teamMatchingId
    ) {
        final TeamMatching teamMatching = getTeamMatching(teamMatchingId);

        return new RequestTeamMatchingMessageResponse(
                teamMatching.getId(),
                teamMatching.getTeamMemberAnnouncement().getTeamProfile().getTeamMiniProfile().getTeamName(),
                teamMatching.getRequestMessage(),
                teamMatching.getSenderType(),
                true
        );
    }


    // 사용
    public SuccessContactResponse getPrivateSuccessContactResponse(
            final Long memberId,
            final Long privateMatchingId
    ) {
        final PrivateMatching privateMatching = getPrivateMatching(privateMatchingId);
        // 보낸 사람의 ID = 열람한 사람의 ID -> Profile 객체의 정보를 가져온다
        if (Objects.equals(privateMatching.getMember().getId(), memberId)) {
            return new SuccessContactResponse(
                    privateMatching.getProfile().getMember().getMemberBasicInform().getMemberName(),
                    privateMatching.getProfile().getMember().getEmail()
            );
        } else {
            // 보낸 사람의 ID != 열람한 사람의 ID -> Member 객체의 정보를 가져온다
            return new SuccessContactResponse(
                    privateMatching.getMember().getMemberBasicInform().getMemberName(),
                    privateMatching.getMember().getEmail()
            );
        }
    }

    // 사용
    public SuccessContactResponse getTeamSuccessContactResponse(
            final Long memberId,
            final Long teamMatchingId
    ) {
        // 전달 받은 PK -> 팀 매칭 객체 확인
        final TeamMatching teamMatching = getTeamMatching(teamMatchingId);
        // 보낸 사람의 ID = 열람한 사람의 ID -> teamMemberAnnouncement 객체의 정보를 가져온다
        if (Objects.equals(teamMatching.getMember().getId(), memberId)) {
            return new SuccessContactResponse(
                    teamMatching.getTeamMemberAnnouncement().getTeamProfile().getTeamMiniProfile().getTeamName(),
                    teamMatching.getTeamMemberAnnouncement().getTeamProfile().getMember().getEmail()
            );
        } else {
            // 보낸 사람의 ID != 열람한 사람의 ID -> Member 객체의 정보를 가져온다
            return new SuccessContactResponse(
                    teamMatching.getMember().getTeamProfile().getTeamMiniProfile().getTeamName(),
                    teamMatching.getMember().getEmail()
            );
        }

    }



    public List<String> getJobRoleNames(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(profile.getId());

        List<JobRole> jobRoleList = profileJobRoleList.stream()
                .map(ProfileJobRole::getJobRole)
                .toList();

        return jobRoleList.stream()
                .map(JobRole::getJobRoleName)
                .toList();
    }

    private List<ProfileJobRole> getProfileJobRoleList(final Long profileId) {
        return profileJobRoleRepository.findAllByProfileId(profileId);
    }

    private TeamMemberAnnouncementJobRole getTeamMemberAnnouncementJobRole(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementJobRoleRepository.findByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_JOB_ROLE));
    }

    public void acceptPrivateMatching(final Long privateMatchingId, final AllowMatchingRequest allowMatchingRequest) {
        final PrivateMatching privateMatching = getPrivateMatching(privateMatchingId);

        if (allowMatchingRequest.getIsAllowMatching()) {
            privateMatching.updateMatchingStatus(true);
        } else {
            privateMatching.updateMatchingStatus(false);
        }
    }

    public void acceptTeamMatching(final Long teamMatchingId, final AllowMatchingRequest allowMatchingRequest) {
        final TeamMatching teamMatching = getTeamMatching(teamMatchingId);
        if (allowMatchingRequest.getIsAllowMatching()) {
            teamMatching.updateMatchingStatus(true);
        } else {
            teamMatching.updateMatchingStatus(false);
        }
    }
}
