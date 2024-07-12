package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.awards.AwardsCreateRequest;
import liaison.linkit.profile.service.AwardsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class AwardsController {
    private final AwardsService awardsService;

    @PostMapping("/private/award")
    @MemberOnly
    public ResponseEntity<Void> createAward(
            @Auth final Accessor accessor,
            @RequestBody @Valid AwardsCreateRequest awardsCreateRequest
    ){
        log.info("수상 항목 생성 요청 발생");
        awardsService.save(accessor.getMemberId(), awardsCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/private/awards")
    @MemberOnly
    public ResponseEntity<Void> createAwards(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<AwardsCreateRequest> awardsCreateRequests
    ){
        log.info("수상 항목 리스트 생성 요청 발생");
        awardsService.saveAll(accessor.getMemberId(), awardsCreateRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
