package liaison.linkit.matching.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static liaison.linkit.global.exception.ExceptionCode.*;
import static liaison.linkit.matching.domain.type.MatchingStatus.REQUESTED;
import static liaison.linkit.matching.domain.type.MatchingType.PROFILE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchingService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final MatchingRepository matchingRepository;

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 이미 애노테이션으로 매칭에 대한 권한을 체킹한 상태이다.
    // 내 이력서에 발생한 매칭 요청을 저장한다.
    public void createProfileMatching(
            final Long memberId,
            // 해당 매칭 요청이 발생한 내 이력서에 대한 미니 프로필 -> 발생시키기
            final Long profileId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        final Member member = getMember(memberId);
        log.info("memberId={}가 매칭 요청을 보냅니다.", memberId);

        // 새로운 매칭 객체를 생성한다.
        final Matching newMatching = new Matching(
                null,
                member,
                // 내 이력서의 프로필 아이디를 저장한다.
                profileId,
                // 내 이력서를 저장한다.
                PROFILE,
                // 요청 메시지를 저장한다
                matchingCreateRequest.getRequestMessage(),
                // 요청 상태로 저장한다.
                REQUESTED,
                LocalDateTime.now()
        );

        log.info("newMatching={}", newMatching.getReceiveMatchingId());
        matchingRepository.save(newMatching);
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
