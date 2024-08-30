package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.awards.Awards;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.awards.AwardsRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.dto.request.awards.AwardsCreateRequest;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AwardsService {

    private final ProfileRepository profileRepository;
    private final AwardsRepository awardsRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 어떤 보유 기술 및 역할 1개만 조회할 때
    private Awards getAwards(final Long awardsId) {
        return awardsRepository.findById(awardsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_AWARDS_BY_ID));
    }

    // 해당 내 이력서로부터 모든 보유 기술들을 조회하려고 할 때
    private List<Awards> getAwardsList(final Long profileId) {
        try {
            return awardsRepository.findAllByProfileId(profileId);
        } catch (Exception e) {
            throw new BadRequestException(NOT_FOUND_AWARDS_LIST_BY_PROFILE_ID);
        }
    }

    // 멤버로부터 프로필 아이디를 조회해서 존재성을 판단
    public void validateAwardsByMember(final Long memberId) {
        if (!awardsRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_AWARDS_LIST_BY_PROFILE_ID);
        }
    }

    // 멤버로부터 프로필 아이디를 조회해서 존재성을 판단
    public void validateAwardsByProfile(final Long profileId) {
        if (!awardsRepository.existsByProfileId(profileId)) {
            throw new AuthException(NOT_FOUND_AWARDS_LIST_BY_PROFILE_ID);
        }
    }

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    public Long save(
            final Long memberId,
            final AwardsCreateRequest awardsCreateRequest
    ) {

        final Profile profile = getProfile(memberId);

        if (profile.getIsAwards()) {
            return saveAwards(profile, awardsCreateRequest);
        } else{
            profile.updateIsAwards(true);
            profile.updateMemberProfileTypeByCompletion();
            return saveAwards(profile, awardsCreateRequest);
        }
    }

    public Long update(
            final Long awardsId,
            final AwardsCreateRequest awardsCreateRequest
    ) {
        final Awards awards = getAwards(awardsId);
        awards.update(awardsCreateRequest);
        return awards.getId();
    }

    // 회원에 대해서 수상 항목 리스트를 저장하는 메서드
    public void saveAll(final Long memberId, final List<AwardsCreateRequest> awardsCreateRequests) {
        final Profile profile = getProfile(memberId);

        // 기존 항목 전체 삭제
        if (awardsRepository.existsByProfileId(profile.getId())) {
            awardsRepository.deleteAllByProfileId(profile.getId());
            profile.updateIsAwards(false);
            profile.updateMemberProfileTypeByCompletion();
        }

        awardsCreateRequests.forEach(request -> {
            saveAwards(profile, request);
        });

        profile.updateIsAwards(true);
        profile.updateMemberProfileTypeByCompletion();
    }

    private Long saveAwards(final Profile profile, final AwardsCreateRequest awardsCreateRequest) {
        final Awards newAwards = Awards.of(
                profile,
                awardsCreateRequest.getAwardsName(),
                awardsCreateRequest.getRanking(),
                awardsCreateRequest.getOrganizer(),
                awardsCreateRequest.getAwardsYear(),
                awardsCreateRequest.getAwardsMonth(),
                awardsCreateRequest.getAwardsDescription()
        );
        return awardsRepository.save(newAwards).getId();
    }

    // 해당 회원의 모든 수상 항목을 조회하는 메서드 / 수정 이전 화면에서 필요
    @Transactional(readOnly = true)
    public List<AwardsResponse> getAllAwards(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<Awards> awards = awardsRepository.findAllByProfileId(profile.getId());
        return awards.stream()
                .map(this::getAwardsResponse)
                .toList();
    }

    // 해당 회원의 모든 수상 항목을 조회하는 메서드 / 수정 이전 화면에서 필요
    @Transactional(readOnly = true)
    public List<AwardsResponse> getAllBrowseAwards(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));


        final List<Awards> awards = awardsRepository.findAllByProfileId(profile.getId());
        return awards.stream()
                .map(this::getAwardsResponse)
                .toList();
    }


    private AwardsResponse getAwardsResponse(final Awards awards) {
        return AwardsResponse.of(awards);
    }

    // 수상 항목 1개 조회 (비공개 처리 로직 추가 필요)
    @Transactional(readOnly = true)
    public AwardsResponse getAwardsDetail(final Long awardsId){
        final Awards awards = awardsRepository.findById(awardsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_AWARDS_ID));
        return AwardsResponse.personalAwards(awards);
    }

    public void deleteAwards(final Long memberId, final Long awardsId) {
        final Profile profile = getProfile(memberId);
        final Awards awards = getAwards(awardsId);

        // 해당 수상 정보 삭제
        awardsRepository.deleteById(awards.getId());

        if (!awardsRepository.existsByProfileId(profile.getId())) {
            // 존재하지 않는 경우
            profile.updateIsAwards(false);
            profile.updateMemberProfileTypeByCompletion();
        }
    }



}
