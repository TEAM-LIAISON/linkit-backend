package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
import liaison.linkit.profile.service.EducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private")
@Slf4j
public class EducationController {

    public final EducationService educationService;


    // 학력 단일 생성
    @PostMapping("/education")
    @MemberOnly
    public ResponseEntity<?> createEducation(
            @Auth final Accessor accessor,
            @RequestBody @Valid EducationCreateRequest educationCreateRequest
    ) {
        log.info("memberId={}의 학력 생성 요청이 들어왔습니다.", accessor.getMemberId());
        final Long educationId = educationService.save(accessor.getMemberId(), educationCreateRequest);
        return ResponseEntity.status(CREATED).body(educationId);
    }

    // 학력 단일 수정
    @PostMapping("/education/{educationId}")
    @MemberOnly
    public ResponseEntity<?> updateEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long educationId,
            @RequestBody @Valid EducationCreateRequest educationCreateRequest
    ) {
        log.info("memberId={}의 educationId={} 수정 요청이 발생하였습니다.", accessor.getMemberId(), educationId);
        final Long updatedEducationId = educationService.update(educationId, educationCreateRequest);
        return ResponseEntity.ok().body(updatedEducationId);
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
