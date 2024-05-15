package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.attach.AttachFileCreateRequest;
import liaison.linkit.profile.dto.request.attach.AttachFileUpdateRequest;
import liaison.linkit.profile.dto.request.attach.AttachUrlCreateRequest;
import liaison.linkit.profile.dto.request.attach.AttachUrlUpdateRequest;
import liaison.linkit.profile.dto.response.Attach.AttachFileResponse;
import liaison.linkit.profile.dto.response.Attach.AttachResponse;
import liaison.linkit.profile.dto.response.Attach.AttachUrlResponse;
import liaison.linkit.profile.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attach")
public class AttachController {

    private final AttachService attachService;

    // 외부 링크 1개 생성 요청
    @PostMapping("/url")
    @MemberOnly
    public ResponseEntity<Void> createAttachUrl(
            @Auth final Accessor accessor,
            @RequestBody @Valid AttachUrlCreateRequest attachUrlCreateRequest
    ) {
        attachService.saveImage(accessor.getMemberId(), attachUrlCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 외부 링크 1개 조회 요청
    @GetMapping("/url")
    @MemberOnly
    public ResponseEntity<AttachUrlResponse> getAttachUrl(
            @Auth final Accessor accessor
    ) {
        Long attachUrlId = attachService.validateAttachUrlByMember(accessor.getMemberId());
        final AttachUrlResponse attachUrlResponse = attachService.getAttachUrlDetail(attachUrlId);
        return ResponseEntity.ok().body(attachUrlResponse);
    }

    // 외부 링크 1개 수정 요청
    @PatchMapping("/url")
    @MemberOnly
    public ResponseEntity<Void> updateAttachUrl(
            @Auth final Accessor accessor,
            @RequestBody @Valid final AttachUrlUpdateRequest updateRequest
    ) {
        attachService.updateImage(accessor.getMemberId(), updateRequest);
        return ResponseEntity.noContent().build();
    }

    // 외부 링크 1개 삭제 요청
    @DeleteMapping("/url")
    @MemberOnly
    public ResponseEntity<Void> deleteAttachUrl(
            @Auth final Accessor accessor
    ) {
        attachService.deleteImage(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/file")
    @MemberOnly
    public ResponseEntity<Void> createAttachFle(
            @Auth final Accessor accessor,
            @RequestBody @Valid final AttachFileCreateRequest createRequest
    ) {
        attachService.saveFile(accessor.getMemberId(), createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/file")
    @MemberOnly
    public ResponseEntity<AttachFileResponse> getAttachFile(
            @Auth final Accessor accessor
    ) {
        Long attachFileId = attachService.validateAttachFileByMember(accessor.getMemberId());
        final AttachFileResponse attachFileResponse = attachService.getAttachFileDetail(attachFileId);
        return ResponseEntity.ok().body(attachFileResponse);
    }

    @PatchMapping("/file")
    @MemberOnly
    public ResponseEntity<Void> updateAttachFile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final AttachFileUpdateRequest updateRequest
    ) {
        attachService.updateFile(accessor.getMemberId(), updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/file")
    @MemberOnly
    public ResponseEntity<Void> deleteAttachFile(
            @Auth final Accessor accessor
    ) {
        attachService.deleteFile(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    @MemberOnly
    public ResponseEntity<AttachResponse> getAttachList(
            @Auth final Accessor accessor
    ) {
        final AttachResponse attachResponse = attachService.getAttachList(accessor.getMemberId());
        return ResponseEntity.ok().body(attachResponse);
    }
}
