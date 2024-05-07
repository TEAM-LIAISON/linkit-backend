package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Awards;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.AwardsRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.AwardsCreateRequest;
import liaison.linkit.profile.dto.request.AwardsUpdateRequest;
import liaison.linkit.profile.dto.response.AwardsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_AWARDS_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_AWARDS_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class AwardsService {

    private final AwardsRepository awardsRepository;
    private final ProfileRepository profileRepository;

    public Long validateAwardsByMember(Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if(!awardsRepository.existsByProfileId(profileId)){
            throw new AuthException(INVALID_AWARDS_WITH_MEMBER);
        } else {
            return awardsRepository.findByProfileId(profileId).getId();
        }
    }

    // 회원에 대해서 수상 항목을 저장하는 메서드
    public void save(final Long memberId, final AwardsCreateRequest awardsCreateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final Awards newAwards = Awards.of(
                profile,
                awardsCreateRequest.getAwardsName(),
                awardsCreateRequest.getRanking(),
                awardsCreateRequest.getOrganizer(),
                awardsCreateRequest.getAwardsYear(),
                awardsCreateRequest.getAwardsMonth(),
                awardsCreateRequest.getAwardsDescription()
        );
        awardsRepository.save(newAwards);
        // 수상 이력이 등록되었음으로 변경
        profile.updateIsAwards(true);
        // 접근 권한 판단 함수 실행
        profile.updateMemberProfileTypeByCompletion();
    }

    // 해당 회원의 모든 수상 항목을 조회하는 메서드 / 수정 이전 화면에서 필요
    @Transactional(readOnly = true)
    public List<AwardsResponse> getAllAwards(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        final List<Awards> awards = awardsRepository.findAllByProfileId(profileId);
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

    public void update(final Long memberId, final AwardsUpdateRequest awardsUpdateRequest){
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long awardsId = validateAwardsByMember(memberId);

        final Awards awards = awardsRepository.findById(awardsId)
                .orElseThrow(() ->  new BadRequestException(NOT_FOUND_AWARDS_ID));

        awards.update(awardsUpdateRequest);
        awardsRepository.save(awards);

        profile.updateMemberProfileTypeByCompletion();
    }

    public void delete(final Long memberId) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long awardsId = validateAwardsByMember(memberId);

        if(!awardsRepository.existsById(awardsId)){
            throw new BadRequestException(NOT_FOUND_AWARDS_ID);
        }
        awardsRepository.deleteById(awardsId);

        profile.updateIsAwards(false);
        profile.updateMemberProfileTypeByCompletion();
    }
}
