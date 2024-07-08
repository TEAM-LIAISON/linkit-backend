package liaison.linkit.matching.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_BY_MEMBER_ID;
import static liaison.linkit.matching.domain.type.MatchingStatus.REQUESTED;
import static liaison.linkit.matching.domain.type.MatchingType.PROFILE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchingService {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    public void createProfileMatching(
            final Long memberId,
            final Long miniProfileId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        // 누구의 매칭 요청인가?
        final Member member = getMember(memberId);
        log.info("memberId={}가 매칭 요청을 보냅니다.", memberId);

        final Matching newMatching = new Matching(
                null,
                member,
                miniProfileId,
                PROFILE,
                matchingCreateRequest.getRequestMessage(),
                REQUESTED,
                LocalDateTime.now()
        );

        log.info("newMatching={}", newMatching.getReceiveMatchingId());
        matchingRepository.save(newMatching);
    }
}
