package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.EducationCreateRequest;
import liaison.linkit.profile.dto.request.EducationUpdateRequest;
import liaison.linkit.profile.dto.response.EducationResponse;
import liaison.linkit.profile.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/education")
public class EducationController {
    private final EducationService educationService;

    // 교육 항목 전체 조회
    @GetMapping("/list")
    @MemberOnly
    public ResponseEntity<List<EducationResponse>> getEducationList(@Auth final Accessor accessor) {
        final List<EducationResponse> educationResponses = educationService.getAllEducations(accessor.getMemberId());
        return ResponseEntity.ok().body(educationResponses);
    }

    // 교육 항목 1개 생성 -> Education 테이블에 저장된 PK를 반환한다.
    @PostMapping
    @MemberOnly
    public ResponseEntity<EducationResponse> createEducation(
            @Auth final Accessor accessor,
            @RequestBody @Valid EducationCreateRequest educationCreateRequest
    ) {
        final EducationResponse educationResponse = educationService.save(accessor.getMemberId(), educationCreateRequest);
        return ResponseEntity.ok().body(educationResponse);
    }

    // 교육 항목 1개 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<EducationResponse> getEducation(@Auth final Accessor accessor) {
        Long educationId = educationService.validateEducationByMember(accessor.getMemberId());
        final EducationResponse educationResponse = educationService.getEducationDetail(educationId);
        return ResponseEntity.ok().body(educationResponse);
    }

    // 교육 항목 1개 수정
    @PutMapping("/{educationId}")
    @MemberOnly
    public ResponseEntity<EducationResponse> updateEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long educationId,
            @RequestBody @Valid final EducationUpdateRequest educationUpdateRequest
    ){
        educationService.validateEducationByMember(accessor.getMemberId());
        EducationResponse educationResponse = educationService.update(educationId, educationUpdateRequest);
        return ResponseEntity.ok().body(educationResponse);
    }

    // 교육 항목 1개 삭제
    @DeleteMapping("/{educationId}")
    @MemberOnly
    public ResponseEntity<Void> deleteEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long educationId
    ) {
        educationService.delete(accessor.getMemberId(), educationId);
        return ResponseEntity.noContent().build();
    }
}
