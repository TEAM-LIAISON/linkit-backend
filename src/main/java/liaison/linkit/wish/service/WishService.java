package liaison.linkit.wish.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.wish.domain.Wish;
import liaison.linkit.wish.domain.repository.WishRepository;
import liaison.linkit.wish.domain.type.WishType;
import liaison.linkit.wish.dto.request.WishProfileCreateRequest;
import liaison.linkit.wish.dto.request.WishTeamProfileCreateRequest;
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
public class WishService {

    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;

    public void createProfileWish(
            final Long memberId,
            final WishProfileCreateRequest wishProfileCreateRequest
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        // 선택한 프로필(내 이력서)에 대한 ID를 가져옴
        final Wish newProfileWish = new Wish(
                null,
                member,
                wishProfileCreateRequest.getReceiveProfileWishId(),
                WishType.PROFILE,
                LocalDateTime.now()
        );

        wishRepository.save(newProfileWish);
    }


    public void createTeamProfileWish(
            final Long memberId,
            final WishTeamProfileCreateRequest wishTeamProfileCreateRequest
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Wish newTeamProfileWish = new Wish(
                null,
                member,
                wishTeamProfileCreateRequest.getReceiveTeamProfileWishId(),
                WishType.TEAM_PROFILE,
                LocalDateTime.now()
        );

        wishRepository.save(newTeamProfileWish);
    }
}
