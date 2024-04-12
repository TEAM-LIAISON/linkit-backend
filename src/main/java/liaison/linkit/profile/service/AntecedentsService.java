package liaison.linkit.profile.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Antecedents;
import liaison.linkit.profile.domain.repository.AntecedentsRepository;
import liaison.linkit.profile.dto.request.AntecedentsCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

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
}
