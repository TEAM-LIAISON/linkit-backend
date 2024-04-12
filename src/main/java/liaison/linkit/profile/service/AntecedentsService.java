package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Antecedents;
import liaison.linkit.profile.domain.repository.AntecedentsRepository;
import liaison.linkit.profile.dto.request.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.AntecedentsUpdateRequest;
import liaison.linkit.profile.dto.response.AntecedentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AntecedentsService {

    private final AntecedentsRepository antecedentsRepository;
    private final MemberRepository memberRepository;

    public Long save(final Long memberId, final AntecedentsCreateRequest antecedentsCreateRequest){
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Antecedents newAntecedents = Antecedents.of(
                member,
                antecedentsCreateRequest.getProjectName(),
                antecedentsCreateRequest.getProjectRole(),
                antecedentsCreateRequest.getStartYear(),
                antecedentsCreateRequest.getStartMonth(),
                antecedentsCreateRequest.getEndYear(),
                antecedentsCreateRequest.getEndMonth(),
                antecedentsCreateRequest.getAntecedentsDescription()
        );

        final Antecedents antecedents = antecedentsRepository.save(newAntecedents);
        return antecedents.getId();
    }

    public Long validateAntecedentsByMember(Long memberId) {
        if (!antecedentsRepository.existsByMemberId(memberId)) {
            throw new AuthException(INVALID_ANTECEDENTS_WITH_MEMBER);
        } else {
            return antecedentsRepository.findByMemberId(memberId).getId();
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
        final List<Antecedents> antecedents = antecedentsRepository.findAllByMemberId(memberId);
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
