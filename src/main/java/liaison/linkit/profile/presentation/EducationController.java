package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.education.EducationListCreateRequest;
import liaison.linkit.profile.service.EducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private")
@Slf4j
public class EducationController {

    public final EducationService educationService;

    // 1.5.6. 학력 생성/수정
    @PostMapping("/education")
    @MemberOnly
    public ResponseEntity<Void> createEducation(
            @Auth final Accessor accessor,
            @RequestBody @Valid EducationListCreateRequest educationListCreateRequest
    ) {
        log.info("memberId={}의 학력 생성/수정 요청이 들어왔습니다.", accessor.getMemberId());
        educationService.save(accessor.getMemberId(), educationListCreateRequest.getEducationList());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/education/{educationId}")
    @MemberOnly
    public ResponseEntity<Void> deleteEducation(
            @Auth final Accessor accessor,
            @PathVariable final Long educationId
    ) {
        educationService.validateEducationByMember(accessor.getMemberId());
        educationService.delete(accessor.getMemberId(), educationId);
        return ResponseEntity.noContent().build();
    }
}
