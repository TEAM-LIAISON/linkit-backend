package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.MiniProfile;
import liaison.linkit.profile.domain.repository.MiniProfileRepository;
import liaison.linkit.profile.dto.request.MiniProfileCreateRequest;
import liaison.linkit.profile.dto.request.MiniProfileUpdateRequest;
import liaison.linkit.profile.dto.response.MiniProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MiniProfileService {

    private final MiniProfileRepository miniProfileRepository;
    private final MemberRepository memberRepository;

    public Long validateMiniProfileByMember(Long memberId) {
        if (!miniProfileRepository.existsByMemberId(memberId)) {
            throw new AuthException(INVALID_MINI_PROFILE_WITH_MEMBER);
        } else {
            return miniProfileRepository.findByMemberId(memberId).getId();
        }
    }

    public MiniProfileResponse save(final Long memberId, final MiniProfileCreateRequest miniProfileCreateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final MiniProfile newMiniProfile = MiniProfile.of(
                member,
                miniProfileCreateRequest.getOneLineIntroduction(),
                miniProfileCreateRequest.getInterests(),
                miniProfileCreateRequest.getFirstFreeText(),
                miniProfileCreateRequest.getSecondFreeText()
        );
        final MiniProfile miniProfile = miniProfileRepository.save(newMiniProfile);
        return getMiniProfileResponse(miniProfile);
    }

    private MiniProfileResponse getMiniProfileResponse(final MiniProfile miniProfile) {
        return MiniProfileResponse.of(miniProfile);
    }

    @Transactional(readOnly = true)
    public MiniProfileResponse getMiniProfileDetail(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_ID));
        return MiniProfileResponse.personalMiniProfile(miniProfile);
    }

    public void update(final Long miniProfileId, final MiniProfileUpdateRequest miniProfileUpdateRequest) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(()-> new BadRequestException(NOT_FOUND_MINI_PROFILE_ID));

        miniProfile.update(miniProfileUpdateRequest);
        miniProfileRepository.save(miniProfile);
    }

    public void delete(final Long miniProfileId){
        if(!miniProfileRepository.existsById(miniProfileId)){
            throw new BadRequestException(NOT_FOUND_MINI_PROFILE_ID);
        }
        miniProfileRepository.deleteById(miniProfileId);
    }
}
