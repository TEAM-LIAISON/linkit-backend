package liaison.linkit.scrap.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.scrap.domain.PrivateScrap;
import liaison.linkit.scrap.presentation.dto.privateScrap.PrivateScrapResponseDTO.AddPrivateScrap;
import liaison.linkit.scrap.presentation.dto.privateScrap.PrivateScrapResponseDTO.RemovePrivateScrap;

@Mapper
public class PrivateScrapMapper {
    public PrivateScrap toPrivate(final Profile profile, final Member member) {
        return PrivateScrap.builder().profile(profile).member(member).build();
    }

    public AddPrivateScrap toAddPrivateScrap(final PrivateScrap privateScrap) {
        return AddPrivateScrap.builder()
                .createdAt(privateScrap.getCreatedAt())
                .build();
    }

    public RemovePrivateScrap toRemovePrivateScrap() {
        return RemovePrivateScrap.builder().build();
    }
}
