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

    public Long validateAntecedentsByMember(Long memberId) {
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
                antecedentsCreateRequest.getEndMonth(),
                antecedentsCreateRequest.getAntecedentsDescription()
        );

        final Antecedents antecedents = antecedentsRepository.save(newAntecedents);
        return getAntecedentsResponse(antecedents);
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

    public void update(final Long antecedentsId, final AntecedentsUpdateRequest antecedentsUpdateRequest) {
        final Antecedents antecedents = antecedentsRepository.findById(antecedentsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ANTECEDENTS_ID));

        antecedents.update(antecedentsUpdateRequest);
        antecedentsRepository.save(antecedents);
    }

    public void delete(final Long antecedentsId) {
        if (!antecedentsRepository.existsById(antecedentsId)) {
            throw new BadRequestException(NOT_FOUND_ANTECEDENTS_ID);
        }
        antecedentsRepository.deleteById(antecedentsId);
    }
}
