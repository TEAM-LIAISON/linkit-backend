package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Antecedents;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsUpdateRequest;
import liaison.linkit.profile.dto.response.AntecedentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_ANTECEDENTS_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_ANTECEDENTS_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class AntecedentsService {

    private final AntecedentsRepository antecedentsRepository;
    private final ProfileRepository profileRepository;

    public void validateAntecedentsByMember(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!antecedentsRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_ANTECEDENTS_WITH_MEMBER);
        }
    }

    @Transactional
    public List<AntecedentsResponse> saveAll(
            final Long memberId,
            final List<AntecedentsCreateRequest> antecedentsCreateRequests
    ) {
        // 리스트 형태로 이력 생성 요청이 들어옴
        // 기존에 저장되어 있던 모든 이력을 삭제하고 다시 생성해줘야 함.
        Profile profile = profileRepository.findByMemberId(memberId);
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found for memberId: " + memberId);
        }

        // 기존에 존재하던 해당 프로필의 모든 이력 항목을 삭제한다.
        if (antecedentsRepository.existsByProfileId(profile.getId())) {
            antecedentsRepository.deleteAllByProfileId(profile.getId());
        }

        // 저장 로직을 반복 실행하여 모든 경력 데이터 저장
        List<Antecedents> savedAntecedents = antecedentsCreateRequests.stream().map(request -> {
            return saveAntecedent(profile, request);
        }).toList();

        // 프로필 업데이트
        profile.updateIsAntecedents(true);
        profile.updateMemberProfileTypeByCompletion();

        // 저장된 각 경력 정보에 대한 응답 생성
        return savedAntecedents.stream()
                .map(this::getAntecedentsResponse)
                .collect(Collectors.toList());
    }

    private Antecedents saveAntecedent(Profile profile, AntecedentsCreateRequest request) {
        Antecedents newAntecedents = Antecedents.of(
                profile,
                request.getProjectName(),
                request.getProjectRole(),
                request.getStartYear(),
                request.getStartMonth(),
                request.getEndYear(),
                request.getEndMonth(),
                request.isRetirement()
        );

        return antecedentsRepository.save(newAntecedents);
    }

    @Transactional(readOnly = true)
    public AntecedentsResponse getAntecedentsDetail(final Long antecedentsId) {
        final Antecedents antecedents = antecedentsRepository.findById(antecedentsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ANTECEDENTS_ID));
        return AntecedentsResponse.personalAntecedents(antecedents);
    }

    @Transactional(readOnly = true)
    public List<AntecedentsResponse> getAllAntecedents(Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        final List<Antecedents> antecedents = antecedentsRepository.findAllByProfileId(profileId);
        return antecedents.stream()
                .map(this::getAntecedentsResponse)
                .toList();
    }

    private AntecedentsResponse getAntecedentsResponse(final Antecedents antecedents) {
        return AntecedentsResponse.of(antecedents);
    }

    public AntecedentsResponse update(final Long memberId, final Long antecedentsId, final AntecedentsUpdateRequest antecedentsUpdateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final Antecedents antecedents = antecedentsRepository.findById(antecedentsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ANTECEDENTS_ID));

        // 입력 받은 요청을 토대로 객체 변환
        antecedents.update(antecedentsUpdateRequest);
        antecedentsRepository.save(antecedents);

        // 완성도 로직
        profile.updateMemberProfileTypeByCompletion();

        // 객체 응답 반환 로직
        return getAntecedentsResponse(antecedents);
    }

    public void delete(final Long memberId, final Long antecedentsId) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        if (!antecedentsRepository.existsById(antecedentsId)) {
            throw new BadRequestException(NOT_FOUND_ANTECEDENTS_ID);
        }

        // 삭제
        antecedentsRepository.deleteById(antecedentsId);

        // 완성도 로직
        profile.updateIsAntecedents(false);
        profile.updateMemberProfileTypeByCompletion();
    }
}
