package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.attach.AttachUrlCreateRequest;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AttachController {

    private final AttachService attachService;

    // 외부 링크 1개 생성 요청
    @PostMapping("/private/attach/url")
    @MemberOnly
    public ResponseEntity<Void> createAttachUrl(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<AttachUrlCreateRequest> attachUrlCreateRequests
    ) {
        attachService.saveUrl(accessor.getMemberId(), attachUrlCreateRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 외부 링크 1개 삭제 요청
    @DeleteMapping("/private/attach/url/{attachUrlId}")
    @MemberOnly
    public ResponseEntity<Void> deleteAttachUrl(
            @Auth final Accessor accessor,
            @PathVariable final Long attachUrlId
    ) {
        attachService.validateAttachUrlByMember(accessor.getMemberId());
        attachService.deleteUrl(accessor.getMemberId(), attachUrlId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/private/attach/url/list")
    @MemberOnly
    public ResponseEntity<AttachResponse> getAttachList(
            @Auth final Accessor accessor
    ) {

        final AttachResponse attachResponse = attachService.getAttachList(accessor.getMemberId());
        return ResponseEntity.ok().body(attachResponse);
    }
}
