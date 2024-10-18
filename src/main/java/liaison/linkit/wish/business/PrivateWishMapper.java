package liaison.linkit.wish.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.presentation.dto.privateWish.PrivateWishResponseDTO;

@Mapper
public class PrivateWishMapper {
    public PrivateWish toPrivate(final Profile profile, final Member member) {
        return PrivateWish.builder().profile(profile).member(member).build();
    }

    public PrivateWishResponseDTO.AddPrivateWish toAddPrivateWish(final PrivateWish privateWish) {

        return PrivateWishResponseDTO.AddPrivateWish.builder()
                .createdAt(privateWish.getCreatedAt())
                .build();
    }

    public PrivateWishResponseDTO.RemovePrivateWish toRemovePrivateWish() {
        return PrivateWishResponseDTO.RemovePrivateWish.builder().build();
    }
}
