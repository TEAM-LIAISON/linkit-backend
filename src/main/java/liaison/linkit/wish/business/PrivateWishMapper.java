package liaison.linkit.wish.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.presentation.dto.privateScrap.PrivateScrapResponseDTO.AddPrivateScrap;
import liaison.linkit.wish.presentation.dto.privateScrap.PrivateScrapResponseDTO.RemovePrivateScrap;

@Mapper
public class PrivateWishMapper {
    public PrivateWish toPrivate(final Profile profile, final Member member) {
        return PrivateWish.builder().profile(profile).member(member).build();
    }

    public AddPrivateScrap toAddPrivateWish(final PrivateWish privateWish) {
        return AddPrivateScrap.builder()
                .createdAt(privateWish.getCreatedAt())
                .build();
    }

    public RemovePrivateScrap toRemovePrivateWish() {
        return RemovePrivateScrap.builder().build();
    }
}
