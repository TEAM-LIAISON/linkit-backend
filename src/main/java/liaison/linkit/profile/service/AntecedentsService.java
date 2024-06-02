package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Antecedents;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.AntecedentsUpdateRequest;
import liaison.linkit.profile.dto.response.AntecedentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_ANTECEDENTS_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_ANTECEDENTS_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class AntecedentsService {

    private final AntecedentsRepository antecedentsRepository;
    private final ProfileRepository profileRepository;

    public Long validateAntecedentsByMember(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!antecedentsRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_ANTECEDENTS_WITH_MEMBER);
        } else {
            return antecedentsRepository.findByProfileId(profileId).getId();
        }
    }

    public AntecedentsResponse save(final Long memberId, final AntecedentsCreateRequest antecedentsCreateRequest) {
        try {
            final Profile profile = profileRepository.findByMemberId(memberId);
            if (profile == null) {
                throw new IllegalArgumentException("Profile not found for memberId: " + memberId);
            }

            final Antecedents newAntecedents = Antecedents.of(
                    profile,
                    antecedentsCreateRequest.getProjectName(),
                    antecedentsCreateRequest.getProjectRole(),
                    antecedentsCreateRequest.getStartYear(),
                    antecedentsCreateRequest.getStartMonth(),
                    antecedentsCreateRequest.getEndYear(),
                    antecedentsCreateRequest.getEndMonth(),
                    antecedentsCreateRequest.isRetirement()
            );

            Antecedents savedAntecedents = antecedentsRepository.save(newAntecedents);

            profile.updateIsAntecedents(true);
            profile.updateMemberProfileTypeByCompletion();

            return getAntecedentsResponse(savedAntecedents);

        } catch (IllegalArgumentException e) {
            // Handle known exceptions here
            throw e;  // or return a custom response or error code

        } catch (Exception e) {
            // Handle unexpected exceptions
            throw new RuntimeException("An unexpected error occurred while saving antecedents data", e);
        }
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
