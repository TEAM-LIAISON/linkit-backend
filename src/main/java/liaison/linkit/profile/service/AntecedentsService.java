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

    public AntecedentsResponse save(final Long memberId, final AntecedentsCreateRequest antecedentsCreateRequest){
        final Profile profile = profileRepository.findByMemberId(memberId);

        final Antecedents newAntecedents = Antecedents.of(
                profile,
                antecedentsCreateRequest.getProjectName(),
                antecedentsCreateRequest.getProjectRole(),
                antecedentsCreateRequest.getStartYear(),
                antecedentsCreateRequest.getStartMonth(),
                antecedentsCreateRequest.getEndYear(),
                antecedentsCreateRequest.getEndMonth()
        );

        Antecedents savedAntecedents = antecedentsRepository.save(newAntecedents);

        profile.updateIsAntecedents(true);
        profile.updateMemberProfileTypeByCompletion();

        return getAntecedentsResponse(savedAntecedents);
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

    public void update(final Long memberId, final AntecedentsUpdateRequest antecedentsUpdateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long antecedentsId = validateAntecedentsByMember(memberId);

        final Antecedents antecedents = antecedentsRepository.findById(antecedentsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ANTECEDENTS_ID));

        // 입력에 대한 제약은 프론트에서 처리해준다.
        // 들어오는 값은 Not null
        antecedents.update(antecedentsUpdateRequest);
        antecedentsRepository.save(antecedents);

        profile.updateMemberProfileTypeByCompletion();
    }

    public void delete(final Long memberId) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        final Long antecedentsId = validateAntecedentsByMember(memberId);

        if (!antecedentsRepository.existsById(antecedentsId)) {
            throw new BadRequestException(NOT_FOUND_ANTECEDENTS_ID);
        }

        antecedentsRepository.deleteById(antecedentsId);

        profile.updateIsAntecedents(false);
        profile.updateMemberProfileTypeByCompletion();
    }
}
