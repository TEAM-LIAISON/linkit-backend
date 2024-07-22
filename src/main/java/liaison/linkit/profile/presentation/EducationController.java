package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
import liaison.linkit.profile.dto.request.education.EducationListCreateRequest;
import liaison.linkit.profile.service.EducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private")
@Slf4j
public class EducationController {

    public final EducationService educationService;

    // 1.5.6. 학력 생성/수정
    @PostMapping("/educations")
    @MemberOnly
    public ResponseEntity<Void> createEducations(
            @Auth final Accessor accessor,
            @RequestBody @Valid EducationListCreateRequest educationListCreateRequest
    ) {
        log.info("memberId={}의 학력 생성/수정 요청이 들어왔습니다.", accessor.getMemberId());
        educationService.saveAll(accessor.getMemberId(), educationListCreateRequest.getEducationList());
        return ResponseEntity.ok().build();
    }

    // 학력 단일 생성
    @PostMapping("/education")
    @MemberOnly
    public ResponseEntity<Void> createEducation(
            @Auth final Accessor accessor,
            @RequestBody @Valid EducationCreateRequest educationCreateRequest
    ) {
        log.info("memberId={}의 학력 생성 요청이 들어왔습니다.", accessor.getMemberId());
        educationService.save(accessor.getMemberId(), educationCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/education/{educationId}")
    @MemberOnly
    public ResponseEntity<Void> updateEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long educationId,
            @RequestBody @Valid EducationCreateRequest educationCreateRequest
    ) {
        log.info("memberId={}의 학력 수정 요청이 들어왔습니다.", accessor.getMemberId());
        educationService.update(educationId, educationCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/education/{educationId}")
    @MemberOnly
    public ResponseEntity<Void> deleteEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long educationId
    ) {
        log.info("educationId={}에 대한 삭제 요청이 들어왔습니다.", educationId);
        educationService.validateEducationByMember(accessor.getMemberId());
        educationService.delete(accessor.getMemberId(), educationId);
        return ResponseEntity.noContent().build();
    }
}
