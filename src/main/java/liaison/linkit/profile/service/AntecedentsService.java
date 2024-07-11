package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.antecedents.Antecedents;
import liaison.linkit.profile.domain.repository.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsUpdateRequest;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AntecedentsService {

    private final ProfileRepository profileRepository;
    private final AntecedentsRepository antecedentsRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 어떤 이력 정보 1개만 조회할 때
    private Antecedents getAntecedents(final Long antecedentsId) {
        return antecedentsRepository.findByProfileId(antecedentsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ANTECEDENTS_BY_ID));
    }

    // 내 이력서 하나에 대한 모든 이력 정보 리스트 조회
    private List<Antecedents> getAntecedentsList(final Long profileId) {
        try {
            return antecedentsRepository.findAllByProfileId(profileId);
        } catch (Exception e) {
            throw new BadRequestException(NOT_FOUND_ANTECEDENTS_BY_PROFILE_ID);
        }
    }

    // 멤버 아이디로부터 이력 항목 유효성 판단
    public void validateAntecedentsByMember(final Long memberId) {
        if (!antecedentsRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_ANTECEDENTS_BY_PROFILE_ID);
        }
    }

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    @Transactional
    public void saveAll(
            final Long memberId,
            final List<AntecedentsCreateRequest> antecedentsCreateRequests
    ) {
        // 리스트 형태로 이력 생성 요청이 들어옴
        // 기존에 저장되어 있던 모든 이력을 삭제하고 다시 생성해줘야 함.
        final Profile profile = getProfile(memberId);

        // 기존에 존재하던 해당 프로필의 모든 이력 항목을 삭제한다.
        if (antecedentsRepository.existsByProfileId(profile.getId())) {
            antecedentsRepository.deleteAllByProfileId(profile.getId());
            profile.updateIsAntecedents(false);
            profile.updateMemberProfileTypeByCompletion();
        }

        // 저장 로직을 반복 실행하여 모든 경력 데이터 저장

        antecedentsCreateRequests.forEach(request -> {
            saveAntecedent(profile, request);
        });

        // 프로필 업데이트
        profile.updateIsAntecedents(true);
        profile.updateMemberProfileTypeByCompletion();
    }

    private void saveAntecedent(final Profile profile, final AntecedentsCreateRequest request) {
        final Antecedents newAntecedents = Antecedents.of(
                profile,
                request.getProjectName(),
                request.getProjectRole(),
                request.getStartYear(),
                request.getStartMonth(),
                request.getEndYear(),
                request.getEndMonth(),
                request.isRetirement(),
                request.getAntecedentsDescription()
        );
        antecedentsRepository.save(newAntecedents);
    }

    @Transactional(readOnly = true)
    public AntecedentsResponse getPersonalAntecedents(final Long antecedentsId) {
        final Antecedents antecedents = antecedentsRepository.findById(antecedentsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ANTECEDENTS_ID));
        return AntecedentsResponse.personalAntecedents(antecedents);
    }

    @Transactional(readOnly = true)
    public List<AntecedentsResponse> getAllAntecedents(Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<Antecedents> antecedents = antecedentsRepository.findAllByProfileId(profile.getId());
        return antecedents.stream()
                .map(this::getAntecedentsResponse)
                .toList();
    }

    private AntecedentsResponse getAntecedentsResponse(final Antecedents antecedents) {
        return AntecedentsResponse.of(antecedents);
    }

    public AntecedentsResponse update(final Long memberId, final Long antecedentsId, final AntecedentsUpdateRequest antecedentsUpdateRequest) {
        final Profile profile = getProfile(memberId);

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
        final Profile profile = getProfile(memberId);
        final Antecedents antecedents = getAntecedents(antecedentsId);


        antecedentsRepository.deleteById(antecedents.getId());
        log.info("삭제 완료");
        if (!antecedentsRepository.existsByProfileId(profile.getId())) {
            profile.updateIsAntecedents(false);
            profile.cancelPerfectionDefault();
            profile.updateMemberProfileTypeByCompletion();
        }
    }

    public void save(
            final Long memberId,
            final AntecedentsCreateRequest antecedentsCreateRequest
    ) {
        final Profile profile = getProfile(memberId);
        // 저장 메서드
        saveAntecedent(profile, antecedentsCreateRequest);
        // 업데이트
        profile.updateIsAntecedents(true);
        profile.addPerfectionDefault();
        profile.updateMemberProfileTypeByCompletion();
    }
}
