//package liaison.linkit.admin.presentation;
//
//import jakarta.validation.Valid;
//import liaison.linkit.admin.dto.request.AdminMemberCreateRequest;
//import liaison.linkit.admin.dto.request.PasswordUpdateRequest;
//import liaison.linkit.admin.dto.response.AdminMemberResponse;
//import liaison.linkit.admin.service.AdminMemberService;
//import liaison.linkit.auth.AdminAuth;
//import liaison.linkit.auth.MasterOnly;
//import liaison.linkit.auth.domain.Accessor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/admin/members")
//public class AdminMemberController {
//
//    private final AdminMemberService adminMemberService;
//
//    @GetMapping
//    @MasterOnly
//    public ResponseEntity<List<AdminMemberResponse>> getAdminMembers(
//            @AdminAuth final Accessor accessor
//    ) {
//        final List<AdminMemberResponse> adminMemberResponses = adminMemberService.getAdminMembers();
//        return ResponseEntity.ok(adminMemberResponses);
//    }
//
//    @PostMapping
//    @MasterOnly
//    public ResponseEntity<Void> createAdminMember(
//            @AdminAuth final Accessor accessor,
//            @RequestBody @Valid final AdminMemberCreateRequest adminMemberCreateRequest
//    ) {
//        final Long memberId = adminMemberService.createAdminMember(adminMemberCreateRequest);
//        return ResponseEntity.created(URI.create("/admin/members/" + memberId)).build();
//    }
//
//    @PatchMapping("/{memberId}/password")
//    @MasterOnly
//    public ResponseEntity<Void> updatePassword(
//            @AdminAuth final Accessor accessor,
//            @PathVariable final Long memberId,
//            @RequestBody @Valid final PasswordUpdateRequest passwordUpdateRequest
//    ) {
//        adminMemberService.updatePassword(memberId, passwordUpdateRequest);
//        return ResponseEntity.noContent().build();
//    }
//}
