package liaison.linkit.scrap.business;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.scrap.business.mapper.ProfileScrapMapper;
import liaison.linkit.scrap.domain.ProfileScrap;
import liaison.linkit.scrap.exception.profileScrap.BadRequestProfileScrapException;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapRequestDTO.UpdateProfileScrapRequest;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO;
import liaison.linkit.scrap.validation.ScrapValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileScrapService {

    private final MemberQueryAdapter memberQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileScrapQueryAdapter profileScrapQueryAdapter;
    private final ProfileScrapCommandAdapter profileScrapCommandAdapter;

    private final ProfileScrapMapper profileScrapMapper;

    private final ScrapValidator scrapValidator;

    // 회원이 프로필 스크랩 버튼을 눌렀을 떄의 메서드
    public ProfileScrapResponseDTO.UpdateProfileScrap updateProfileScrap(
            final Long memberId,
            final String emailId,
            final UpdateProfileScrapRequest updateProfileScrapRequest
    ) {

        boolean shouldAddScrap = updateProfileScrapRequest.isChangeScrapValue();

        scrapValidator.validateSelfProfileScrap(memberId, emailId);     // 자기 자신의 프로필 선택에 대한 예외 처리
        scrapValidator.validateMemberMaxProfileScrap(memberId);         // 최대 프로필 스크랩 개수에 대한 예외 처리

        boolean scrapExists = profileScrapQueryAdapter.existsByMemberIdAndEmailId(memberId, emailId);

        if (scrapExists) {
            handleExistingScrap(memberId, emailId, shouldAddScrap);
        } else {
            handleNonExistingScrap(memberId, emailId, shouldAddScrap);
        }

        return profileScrapMapper.toUpdateProfileScrap(emailId, shouldAddScrap);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(Long memberId, String emailId, boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            profileScrapCommandAdapter.deleteByMemberIdAndEmailId(memberId, emailId);
        } else {
            throw BadRequestProfileScrapException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(Long memberId, String emailId, boolean shouldAddScrap) {
        if (shouldAddScrap) {
            Member member = memberQueryAdapter.findById(memberId);
            Profile profile = profileQueryAdapter.findByEmailId(emailId);
            ProfileScrap profileScrap = new ProfileScrap(null, member, profile);
            profileScrapCommandAdapter.addProfileScrap(profileScrap);
        } else {
            throw BadRequestProfileScrapException.EXCEPTION;
        }
    }

}

