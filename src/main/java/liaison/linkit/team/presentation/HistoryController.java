package liaison.linkit.team.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.response.HistoryResponse;
import liaison.linkit.team.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;
//
//    @GetMapping("/list")
//    @MemberOnly
//    public ResponseEntity<List<HistoryResponse>> get


    @GetMapping
    @MemberOnly
    public ResponseEntity<HistoryResponse> getHistory(
            @Auth final Accessor accessor
    ) {
        Long historyId = historyService.validateHistoryByMember(accessor.getMemberId());
        final HistoryResponse historyResponse = historyService.getHistoryDetail(historyId);
        return ResponseEntity.ok().body(historyResponse);
    }
}
