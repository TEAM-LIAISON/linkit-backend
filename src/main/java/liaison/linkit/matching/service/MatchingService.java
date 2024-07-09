package liaison.linkit.matching.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.matching.domain.PrivateMatching;
import liaison.linkit.matching.domain.TeamMatching;
import liaison.linkit.matching.domain.repository.PrivateMatchingRepository;
import liaison.linkit.matching.domain.repository.TeamMatchingRepository;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    private final TeamProfileRepository teamProfileRepository;
    private final TeamMatchingRepository teamMatchingRepository;

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
                // 내 이력서로 요청을 보냈음을 저장
                PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED,
                LocalDateTime.now()
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
                // 내 이력서로 요청을 보냈음을 저장
                TEAM_PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED,
                LocalDateTime.now()
        );

        privateMatchingRepository.save(newPrivateMatching);
    }

    // 3번
    // 팀 소개서가 팀 소개서에 요청을 보낸 경우
    public void createTeamProfileMatchingToTeam(
            final Long memberId,
            final Long teamProfileId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        final Member member = getMember(memberId);
        final TeamProfile teamProfile = getTeamProfileById(teamProfileId);
        final TeamMatching newTeamMatching = new TeamMatching(
                null,
                // 요청 보낸 회원
                member,
                // 내 이력서의 프로필 아이디를 저장한다.
                teamProfile,
                // 내 이력서로 요청을 보냈음을 저장
                TEAM_PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED,
                LocalDateTime.now()
        );
    }


    // 4번
    // 내 이력서가 팀 소개서에 요청을 보낸 경우
    public void createPrivateProfileMatchingToTeam(
            final Long memberId,
            final Long teamProfileId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        final Member member = getMember(memberId);
        final TeamProfile teamProfile = getTeamProfileById(teamProfileId);
        final TeamMatching newTeamMatching = new TeamMatching(
                null,
                // 요청 보낸 회원
                member,
                // 내 이력서의 프로필 아이디를 저장한다.
                teamProfile,
                // 내 이력서로 요청을 보냈음을 저장
                PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED,
                LocalDateTime.now()
        );
    }




//    public List<ReceivedMatchingResponse> getReceivedMatching(
//            final Long memberId
//    ) {
//        // 해당 memberId가 received ID와 일치하는 매칭 객체를 찾아서 반환한다,
//        // receive_matching_id가 미니 프로필 아이디와 동일하다
//        final Profile profile = getProfile(memberId);
//        final Matching matching = getMatchingByProfileId(profile.getId());
//
//
//    }

//    private Matching getMatchingByProfileId(
//            final Long profileId
//    ) {
//        return matchingRepository.findByReceiveMatchingId(profileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MATCHING_BY_PROFILE_ID));
//    }
}
