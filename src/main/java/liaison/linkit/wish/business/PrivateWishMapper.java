package liaison.linkit.wish.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.wish.domain.PrivateWish;

@Mapper
public class PrivateWishMapper {
    public PrivateWish toPrivate(final Profile profile, final Member member) {
        return PrivateWish.builder().profile(profile).member(member).build();
    }
}
