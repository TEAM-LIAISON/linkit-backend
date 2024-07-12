package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsCreateRequest;
import liaison.linkit.profile.service.AntecedentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private")
@Slf4j
public class AntecedentsController {

    public final AntecedentsService antecedentsService;

    // 1.5.7. 경력 리스트 생성/수정
    // 온보딩 이력 생성 요청
    @PostMapping("/antecedents")
    @MemberOnly
    public ResponseEntity<Void> createAntecedents(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<AntecedentsCreateRequest> antecedentsCreateRequests
    ){
        log.info("이력 생성 요청 발생");
        antecedentsService.saveAll(accessor.getMemberId(), antecedentsCreateRequests);
        return ResponseEntity.ok().build();
    }

    // 1.5.7 단일 경력 생성
    @PostMapping("/antecedent")
    @MemberOnly
    public ResponseEntity<Void> createAntecedent(
            @Auth final Accessor accessor,
            @RequestBody @Valid AntecedentsCreateRequest antecedentsCreateRequest
    ) {
        antecedentsService.save(accessor.getMemberId(), antecedentsCreateRequest);
        return ResponseEntity.ok().build();
    }


    // 1.5.7. 단일 경력 수정
    @PostMapping("/antecedents/{antecedentsId}")
    @MemberOnly
    public ResponseEntity<Void> updateAntecedent(
            @Auth final Accessor accessor,
            @RequestBody @Valid AntecedentsCreateRequest antecedentsCreateRequest,
            @PathVariable final Long antecedentsId
    ) {
        antecedentsService.validateAntecedentsByMember(accessor.getMemberId());
        antecedentsService.update(antecedentsId, antecedentsCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/antecedents/{antecedentsId}")
    @MemberOnly
    public ResponseEntity<Void> deleteAntecedents(
            @Auth final Accessor accessor,
            @PathVariable final Long antecedentsId
    ) {
        antecedentsService.validateAntecedentsByMember(accessor.getMemberId());
        antecedentsService.delete(accessor.getMemberId(), antecedentsId);
        return ResponseEntity.noContent().build();
    }
}
