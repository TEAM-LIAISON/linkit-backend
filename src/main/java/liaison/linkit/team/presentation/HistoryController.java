package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.HistoryCreateRequest;
import liaison.linkit.team.service.HistoryService;
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
public class HistoryController {

    private final HistoryService historyService;

    // 단일 연혁 생성
    @PostMapping("/team/history")
    @MemberOnly
    public ResponseEntity<?> createHistory(
            @Auth final Accessor accessor,
            @RequestBody @Valid HistoryCreateRequest historyCreateRequest
    ) {
        log.info("memberId={}의 연혁 생성 요청이 들어왔습니다.", accessor.getMemberId());
        final Long historyId = historyService.saveHistory(accessor.getMemberId(), historyCreateRequest);
        return ResponseEntity.status(CREATED).body(historyId);
    }

    @PostMapping("/team/history/{historyId}")
    @MemberOnly
    public ResponseEntity<?> updateHistory(
            @Auth final Accessor accessor,
            @PathVariable final Long historyId,
            @RequestBody @Valid HistoryCreateRequest historyCreateRequest
    ) {
        log.info("memberId={}의 historyId={} 수정 요청이 발생하였습니다.", accessor.getMemberId(), historyId);
        historyService.validateHistoryByMember(accessor.getMemberId());
        final Long updatedHistoryId = historyService.updateHistory(historyId, historyCreateRequest);
        return ResponseEntity.ok().body(updatedHistoryId);
    }

    @PostMapping("/team/histories")
    @MemberOnly
    public ResponseEntity<Void> createHistories(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<HistoryCreateRequest> historyCreateRequests
    ) {
        historyService.saveHistories(accessor.getMemberId(), historyCreateRequests);
        return ResponseEntity.status(CREATED).build();
    }

    @DeleteMapping("/team/history/{historyId}")
    @MemberOnly
    public ResponseEntity<Void> deleteHistory(
            @Auth final Accessor accessor,
            @PathVariable final Long historyId
    ) {
        historyService.validateHistoryByMember(accessor.getMemberId());
        historyService.deleteHistory(accessor.getMemberId(), historyId);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<HistoryResponse> getHistory(
//            @Auth final Accessor accessor
//    ) {
//        historyService.validateHistoryByMember(accessor.getMemberId());
//        final HistoryResponse historyResponse = historyService.getHistoryDetail(accessor.getMemberId());
//        return ResponseEntity.ok().body(historyResponse);
//    }

//    @PatchMapping
//    @MemberOnly
//    public ResponseEntity<Void> updateHistory(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid HistoryUpdateRequest historyUpdateRequest
//    ) {
//        historyService.update(accessor.getMemberId(), historyUpdateRequest);
//        return ResponseEntity.noContent().build();
//    }
//

}
