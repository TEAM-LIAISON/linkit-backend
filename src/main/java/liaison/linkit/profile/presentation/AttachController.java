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
@RequestMapping("/attach")
public class AttachController {

    private final AttachService attachService;

    // 외부 링크 1개 생성 요청
    @PostMapping("/url")
    @MemberOnly
    public ResponseEntity<Void> createAttachUrl(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<AttachUrlCreateRequest> attachUrlCreateRequests
    ) {
        attachService.saveUrl(accessor.getMemberId(), attachUrlCreateRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 외부 링크 1개 조회 요청
//    @GetMapping("/url")
//    @MemberOnly
//    public ResponseEntity<AttachUrlResponse> getAttachUrl(
//            @Auth final Accessor accessor
//    ) {
//        attachService.validateAttachUrlByMember(accessor.getMemberId());
//        final AttachUrlResponse attachUrlResponse = attachService.getAttachUrlDetail(accessor.getMemberId());
//        return ResponseEntity.ok().body(attachUrlResponse);
//    }

    // 외부 링크 1개 수정 요청
//    @PutMapping("/url/{attachUrlId}")
//    @MemberOnly
//    public ResponseEntity<Void> updateAttachUrl(
//            @Auth final Accessor accessor,
//            @PathVariable final Long attachUrlId,
//            @RequestBody @Valid final AttachUrlUpdateRequest updateRequest
//    ) {
//        // 유효성 검사 먼저
//        attachService.validateAttachUrlByMember(accessor.getMemberId());
//        attachService.updateUrl(attachUrlId, updateRequest);
//        return ResponseEntity.noContent().build();
//    }

    // 외부 링크 1개 삭제 요청
    @DeleteMapping("/url/{attachUrlId}")
    @MemberOnly
    public ResponseEntity<Void> deleteAttachUrl(
            @Auth final Accessor accessor,
            @PathVariable final Long attachUrlId
    ) {
        attachService.validateAttachUrlByMember(accessor.getMemberId());
        attachService.deleteUrl(accessor.getMemberId(), attachUrlId);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/file")
//    @MemberOnly
//    public ResponseEntity<Void> createAttachFle(
//            @Auth final Accessor accessor,
//            @RequestPart MultipartFile attachFile
//    ) {
//        attachService.saveFile(accessor.getMemberId(), attachFile);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

//    @DeleteMapping("/file/{attachFileUrlId}")
//    @MemberOnly
//    public ResponseEntity<Void> deleteAttachFile(
//            @Auth final Accessor accessor,
//            @PathVariable final Long attachFileUrlId
//    ) {
//        attachService.validateAttachFileByMember(accessor.getMemberId());
//        attachService.deleteFile(accessor.getMemberId(), attachFileUrlId);
//        return ResponseEntity.noContent().build();
//    }

//    @GetMapping("/file")
//    @MemberOnly
//    public ResponseEntity<AttachFileResponse> getAttachFile(
//            @Auth final Accessor accessor
//    ) {
//        attachService.validateAttachFileByMember(accessor.getMemberId());
//        final AttachFileResponse attachFileResponse = attachService.getAttachFileDetail(accessor.getMemberId());
//        return ResponseEntity.ok().body(attachFileResponse);
//    }

//
//    @PatchMapping("/file")
//    @MemberOnly
//    public ResponseEntity<Void> updateAttachFile(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid final AttachFileUpdateRequest updateRequest
//    ) {
//        attachService.updateFile(accessor.getMemberId(), updateRequest);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/file")
//    @MemberOnly
//    public ResponseEntity<Void> deleteAttachFile(
//            @Auth final Accessor accessor
//    ) {
//        attachService.deleteFile(accessor.getMemberId());
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/list")
    @MemberOnly
    public ResponseEntity<AttachResponse> getAttachList(
            @Auth final Accessor accessor
    ) {

        final AttachResponse attachResponse = attachService.getAttachList(accessor.getMemberId());
        return ResponseEntity.ok().body(attachResponse);
    }
}
