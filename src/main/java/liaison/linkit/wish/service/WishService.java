package liaison.linkit.wish.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.wish.domain.Wish;
import liaison.linkit.wish.domain.repository.WishRepository;
import liaison.linkit.wish.domain.type.WishType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class WishService {

    private final MemberRepository memberRepository;
    private final WishRepository wishRepository;

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    public void createProfileWish(
            final Long memberId,
            final Long profileId
    ) {
        final Member member = getMember(memberId);
        // 선택한 프로필(내 이력서)에 대한 ID를 가져옴
        final Wish newProfileWish = new Wish(
                null,
                member,
                profileId,
                WishType.PROFILE,
                LocalDateTime.now()
        );
        wishRepository.save(newProfileWish);
    }


    public void createTeamProfileWish(
            final Long memberId,
            final Long teamProfileId
    ) {
        final Member member = getMember(memberId);
        final Wish newTeamProfileWish = new Wish(
                null,
                member,
                teamProfileId,
                WishType.TEAM_PROFILE,
                LocalDateTime.now()
        );
        wishRepository.save(newTeamProfileWish);
    }

//    public void deleteProfileWish(
//            final Long memberId,
//            final Long profileId
//    ) {
//        final Member member = getMember(memberId);
//        wishRepository.delete();
//    }
}
