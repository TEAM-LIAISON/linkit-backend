package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsUpdateRequest;
import liaison.linkit.profile.dto.response.AntecedentsResponse;
import liaison.linkit.profile.service.AntecedentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/antecedents")
@Slf4j
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
    public ResponseEntity<List<AntecedentsResponse>> createAntecedents(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<AntecedentsCreateRequest> antecedentsCreateRequests
    ){
        log.info("이력 생성 요청 발생");
        final List<AntecedentsResponse> antecedentsResponses = antecedentsService.saveAll(accessor.getMemberId(), antecedentsCreateRequests);
        return ResponseEntity.ok().body(antecedentsResponses);
    }

//    // 이력 1개 조회 요청
//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<AntecedentsResponse> getAntecedents(
//            @Auth final Accessor accessor
//    ) {
//        antecedentsService.validateAntecedentsByMember(accessor.getMemberId());
//        final AntecedentsResponse antecedentsResponse = antecedentsService.getAntecedentsDetail(antecedentsId);
//        return ResponseEntity.ok().body(antecedentsResponse);
//    }

    // 이력 1개 수정 요청
    @PutMapping("/{antecedentsId}")
    @MemberOnly
    public ResponseEntity<AntecedentsResponse> updateAntecedents(
        @Auth final Accessor accessor,
        @PathVariable final Long antecedentsId,
        @RequestBody @Valid final AntecedentsUpdateRequest antecedentsUpdateRequest
    ){
        final AntecedentsResponse antecedentsResponse = antecedentsService.update(accessor.getMemberId(), antecedentsId, antecedentsUpdateRequest);
        return ResponseEntity.ok().body(antecedentsResponse);
    }

    // 이력 1개 삭제 요청
    @DeleteMapping("/{antecedentsId}")
    @MemberOnly
    public ResponseEntity<Void> deleteAntecedents(
            @Auth final Accessor accessor,
            @PathVariable final Long antecedentsId
    ) {
        antecedentsService.delete(accessor.getMemberId(), antecedentsId);
        return ResponseEntity.noContent().build();
    }
}
