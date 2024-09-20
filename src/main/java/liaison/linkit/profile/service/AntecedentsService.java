package liaison.linkit.profile.service;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_ANTECEDENTS_BY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_ANTECEDENTS_BY_PROFILE_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_ANTECEDENTS_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;

import java.util.List;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.antecedents.Antecedents;
import liaison.linkit.profile.domain.repository.antecedents.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return antecedentsRepository.findById(antecedentsId)
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

    public void validateAntecedentsByProfile(final Long profileId) {
        if (!antecedentsRepository.existsByProfileId(profileId)) {
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
        }

        // 저장 로직을 반복 실행하여 모든 경력 데이터 저장

        antecedentsCreateRequests.forEach(request -> {
            saveAntecedent(profile, request);
        });

        // 프로필 업데이트
        profile.updateIsAntecedents(true);
    }

    // DB 저장 로직
    private Long saveAntecedent(final Profile profile, final AntecedentsCreateRequest request) {
        log.info("request.getProjectName()={}", request.getProjectName());
        log.info("request.getProjectRole()={}", request.getProjectRole());
        final Antecedents newAntecedents = Antecedents.of(
                profile,
                request.getProjectName(),
                request.getProjectRole(),
                request.getStartDate(),
                request.getEndDate(),
                request.isRetirement(),
                request.getAntecedentsDescription()
        );
        return antecedentsRepository.save(newAntecedents).getId();
    }

    @Transactional(readOnly = true)
    public AntecedentsResponse getPersonalAntecedents(final Long antecedentsId) {
        final Antecedents antecedents = antecedentsRepository.findById(antecedentsId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ANTECEDENTS_ID));
        return AntecedentsResponse.personalAntecedents(antecedents);
    }

    @Transactional(readOnly = true)
    public List<AntecedentsResponse> getAllAntecedents(final Long memberId) {
        final Profile profile = getProfile(memberId);
        final List<Antecedents> antecedents = antecedentsRepository.findAllByProfileId(profile.getId());
        return antecedents.stream()
                .map(this::getAntecedentsResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AntecedentsResponse> getAllBrowseAntecedents(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));

        final List<Antecedents> antecedents = antecedentsRepository.findAllByProfileId(profile.getId());
        return antecedents.stream()
                .map(this::getAntecedentsResponse)
                .toList();
    }

    private AntecedentsResponse getAntecedentsResponse(final Antecedents antecedents) {
        return AntecedentsResponse.of(antecedents);
    }

    // update 메서드
    public Long update(final Long antecedentsId, final AntecedentsCreateRequest antecedentsCreateRequest) {
        final Antecedents antecedents = getAntecedents(antecedentsId);
        // 해당 객체 업데이트
        antecedents.update(antecedentsCreateRequest);
        return antecedents.getId();
    }

    // 삭제 메서드
    public void delete(final Long memberId, final Long antecedentsId) {
        final Profile profile = getProfile(memberId);
        final Antecedents antecedents = getAntecedents(antecedentsId);

        // 조회한 경력을 삭제함
        antecedentsRepository.deleteById(antecedents.getId());
        log.info("삭제 완료");

        // 존재하지 않는다면
        if (!antecedentsRepository.existsByProfileId(profile.getId())) {
            log.info("더 이상 경력이 존재하지 않습니다.");
            // 더 이상 경력이 존재하지 않다면
            profile.updateIsAntecedents(false);
        }
        log.info("profile.getId={}의 경력이 아직 존재합니다.", profile.getId());
    }

    public Long save(
            final Long memberId,
            final AntecedentsCreateRequest antecedentsCreateRequest
    ) {
        final Profile profile = getProfile(memberId);
        // 해당 프로필이 경력을 보유하고 있는 경우
        if (profile.getIsAntecedents()) {
            // 그냥 저장
            return saveAntecedent(profile, antecedentsCreateRequest);
        } else {
            // 경력을 보유하고 있지 않았던 경우
            profile.updateIsAntecedents(true);
            return saveAntecedent(profile, antecedentsCreateRequest);
        }
    }
}
