package liaison.linkit.matching.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchingService {

    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;

    public void createProfileMatching(
            final Long memberId,
            final MatchingCreateRequest matchingCreateRequest
    ) {
        log.info("memberId={}", memberId);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Matching newMatching = new Matching(
                null,
                member,
                matchingCreateRequest.getReceiveMatchingId(),
                MatchingType.PROFILE,
                matchingCreateRequest.getRequestMessage(),
                LocalDateTime.now()
        );

        matchingRepository.save(newMatching);
    }
}
