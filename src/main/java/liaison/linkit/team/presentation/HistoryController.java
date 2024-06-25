package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.HistoryCreateRequest;
import liaison.linkit.team.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/history")
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createHistory(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<HistoryCreateRequest> historyCreateRequests
    ) {
        historyService.saveHistories(accessor.getMemberId(), historyCreateRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
//    @DeleteMapping
//    @MemberOnly
//    public ResponseEntity<Void> deleteHistory(
//            @Auth final Accessor accessor
//    ) {
//        historyService.delete(accessor.getMemberId());
//        return ResponseEntity.noContent().build();
//    }
}
