package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Awards;
import liaison.linkit.profile.domain.repository.AwardsRepository;
import liaison.linkit.profile.dto.request.AwardsCreateRequest;
import liaison.linkit.profile.dto.request.AwardsUpdateRequest;
import liaison.linkit.profile.dto.response.AwardsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AwardsService {

    private final AwardsRepository awardsRepository;
    private final MemberRepository memberRepository;

    public Long validateAwardsByMember(Long memberId) {
        if(!awardsRepository.existsByMemberId(memberId)){
            throw new AuthException(INVALID_AWARDS_WITH_MEMBER);
        } else {
            return awardsRepository.findByMemberId(memberId).getId();
        }
    }

    // 회원에 대해서 수상 이력을 저장하는 메서드
    public AwardsResponse save(final Long memberId, final AwardsCreateRequest awardsCreateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final Awards newAwards = Awards.of(
                member,
                awardsCreateRequest.getAwardsName(),
                awardsCreateRequest.getRanking(),
                awardsCreateRequest.getOrganizer(),
                awardsCreateRequest.getAwardsYear(),
                awardsCreateRequest.getAwardsMonth(),
                awardsCreateRequest.getAwardsDescription()
        );
        final Awards awards = awardsRepository.save(newAwards);
        return getAwardsResponse(awards);

    }

    // 해당 회원의 모든 수상 이력을 조회하는 메서드 / 수정 이전 화면에서 필요
    @Transactional(readOnly = true)
    public List<AwardsResponse> getAllAwards(final Long memberId) {
        final List<Awards> awards = awardsRepository.findAllByMemberId(memberId);
        return awards.stream()
                .map(this::getAwardsResponse)
                .toList();
    }


    private AwardsResponse getAwardsResponse(final Awards awards) {
        return AwardsResponse.of(awards);
    }

    //
    @Transactional(readOnly = true)
    public AwardsResponse getAwardsDetail(final Long awardsId){
        final Awards awards = awardsRepository.findById(awardsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_AWARDS_ID));
        return AwardsResponse.personalAwards(awards);
    }

    public void update(final Long awardsId, final AwardsUpdateRequest awardsUpdateRequest){
        final Awards awards = awardsRepository.findById(awardsId)
                .orElseThrow(() ->  new BadRequestException(NOT_FOUND_AWARDS_ID));

        awards.update(awardsUpdateRequest);
        awardsRepository.save(awards);
    }

    public void delete(final Long awardsId) {
        if(!awardsRepository.existsById(awardsId)){
            throw new BadRequestException(NOT_FOUND_AWARDS_ID);
        }
        awardsRepository.deleteById(awardsId);
    }
}
