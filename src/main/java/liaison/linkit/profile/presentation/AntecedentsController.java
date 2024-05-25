package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.AntecedentsUpdateRequest;
import liaison.linkit.profile.dto.response.AntecedentsResponse;
import liaison.linkit.profile.service.AntecedentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/antecedents")
public class AntecedentsController {

    private final AntecedentsService antecedentsService;

    @GetMapping("/list")
    @MemberOnly
    public ResponseEntity<List<AntecedentsResponse>> getAntecedentsList(@Auth final Accessor accessor) {
        final List<AntecedentsResponse> antecedentsResponses = antecedentsService.getAllAntecedents(accessor.getMemberId());
        return ResponseEntity.ok().body(antecedentsResponses);
    }

    // 이력 1개 생성 요청
    @PostMapping
    @MemberOnly
    public ResponseEntity<AntecedentsResponse> createAntecedents(
            @Auth final Accessor accessor,
            @RequestBody @Valid AntecedentsCreateRequest antecedentsCreateRequest
    ){
        AntecedentsResponse antecedentsResponse = antecedentsService.save(accessor.getMemberId(), antecedentsCreateRequest);
        return ResponseEntity.ok().body(antecedentsResponse);
    }

    // 이력 1개 조회 요청
    @GetMapping
    @MemberOnly
    public ResponseEntity<AntecedentsResponse> getAntecedents(
            @Auth final Accessor accessor
    ) {
        Long antecedentsId = antecedentsService.validateAntecedentsByMember(accessor.getMemberId());
        final AntecedentsResponse antecedentsResponse = antecedentsService.getAntecedentsDetail(antecedentsId);
        return ResponseEntity.ok().body(antecedentsResponse);
    }

    // 이력 1개 수정 요청
    @PatchMapping
    @MemberOnly
    public ResponseEntity<Void> updateAntecedents(
        @Auth final Accessor accessor,
        @RequestBody @Valid final AntecedentsUpdateRequest antecedentsUpdateRequest
    ){
        antecedentsService.update(accessor.getMemberId(), antecedentsUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    // 이력 1개 삭제 요청
    @DeleteMapping
    @MemberOnly
    public ResponseEntity<Void> deleteAntecedents(@Auth final Accessor accessor) {
        antecedentsService.delete(accessor.getMemberId());

        return ResponseEntity.noContent().build();
    }
}
