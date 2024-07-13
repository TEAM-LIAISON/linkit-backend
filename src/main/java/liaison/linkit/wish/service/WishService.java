package liaison.linkit.wish.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.repository.PrivateWishRepository;
import liaison.linkit.wish.domain.type.WishType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_BY_MEMBER_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MINI_PROFILE_BY_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WishService {

    private final MemberRepository memberRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final PrivateWishRepository privateWishRepository;

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    private Profile getProfileByMiniProfileId(
            final Long miniProfileId
    ) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));

        return miniProfile.getProfile();
    }

    public void createWishToPrivateProfile(
            final Long memberId,
            final Long miniProfileId
    ) {
        final Member member = getMember(memberId);
        final Profile profile = getProfileByMiniProfileId(miniProfileId);

        final PrivateWish privateWish = new PrivateWish(
                null,
                member,
                profile,
                WishType.PROFILE,
                LocalDateTime.now()
        );

        privateWishRepository.save(privateWish);
    }


}
