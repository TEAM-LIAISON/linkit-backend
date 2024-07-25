package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.awards.AwardsCreateRequest;
import liaison.linkit.profile.service.AwardsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class AwardsController {
    private final AwardsService awardsService;

    // 단일 수상 생성
    @PostMapping("/private/award")
    @MemberOnly
    public ResponseEntity<?> createAward(
            @Auth final Accessor accessor,
            @RequestBody @Valid AwardsCreateRequest awardsCreateRequest
    ){
        log.info("memberId={}의 수상 생성 요청이 들어왔습니다.", accessor.getMemberId());
        final Long awardsId = awardsService.save(accessor.getMemberId(), awardsCreateRequest);
        return ResponseEntity.status(CREATED).body(awardsId);
    }

    // 단일 수상 수정
    @PostMapping("/private/award/{awardsId}")
    @MemberOnly
    public ResponseEntity<?> updateAward(
            @Auth final Accessor accessor,
            @PathVariable final Long awardsId,
            @RequestBody @Valid AwardsCreateRequest awardsCreateRequest
    ) {
        log.info("memberId={}의 awardsId={} 수정 요청이 발생하였습니다.", accessor.getMemberId(), awardsId);
        final Long updatedAwardsId = awardsService.update(awardsId, awardsCreateRequest);
        return ResponseEntity.ok().body(updatedAwardsId);
    }

    @PostMapping("/private/awards")
    @MemberOnly
    public ResponseEntity<Void> createAwards(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<AwardsCreateRequest> awardsCreateRequests
    ){
        log.info("수상 항목 리스트 생성 요청 발생");
        awardsService.saveAll(accessor.getMemberId(), awardsCreateRequests);
        return ResponseEntity.status(CREATED).build();
    }

    @DeleteMapping("/private/awards/{awardsId}")
    @MemberOnly
    public ResponseEntity<Void> deleteAwards(
            @Auth final Accessor accessor,
            @PathVariable final Long awardsId
    ) {
        awardsService.validateAwardsByMember(accessor.getMemberId());
        awardsService.deleteAwards(accessor.getMemberId(), awardsId);
        return ResponseEntity.noContent().build();
    }
}
