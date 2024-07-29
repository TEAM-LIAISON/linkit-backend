package liaison.linkit.matching.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.matching.CheckMatchingToPrivateProfileAccess;
import liaison.linkit.matching.CheckMatchingToTeamProfileAccess;
import liaison.linkit.matching.dto.request.AllowMatchingRequest;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.dto.response.ReceivedMatchingResponse;
import liaison.linkit.matching.dto.response.RequestMatchingResponse;
import liaison.linkit.matching.dto.response.SuccessMatchingResponse;
import liaison.linkit.matching.dto.response.contact.SuccessContactResponse;
import liaison.linkit.matching.dto.response.existence.ExistenceProfileResponse;
import liaison.linkit.matching.dto.response.messageResponse.*;
import liaison.linkit.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class MatchingController {

    public final MatchingService matchingService;

    // 매칭 요청을 보내기 전에 GET 메서드로 해당 사용자의 권한을 체킹?
    // 아니면 요청이 발생했을 때 사용자의 권한을 조회하는 방법?

    // 개인 이력서로 개인 이력서에 매칭 요청을 보내는 경우
    // 해당 개인 이력서의 PK id가 필요하다.
    // accessor.getMemberId -> 해당 회원의 내 이력서와 상대의 내 이력서가 매칭
    @PostMapping("/private/profile/matching/private/{profileId}")
    @MemberOnly
    @CheckMatchingToPrivateProfileAccess
    public ResponseEntity<Void> createPrivateProfileMatchingToPrivate(
            @Auth final Accessor accessor,
            @PathVariable final Long profileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        log.info("profileId={}에게 내 이력서 대상으로 memberId={} 매칭 요청이 발생했습니다.", profileId, accessor.getMemberId());
        matchingService.createPrivateProfileMatchingToPrivate(accessor.getMemberId(), profileId, matchingCreateRequest);
        return ResponseEntity.status(CREATED).build();
    }

    // accessor.getMemberId -> 해당 회원의 팀 소개서와 상대의 내 이력서가 매칭
    @PostMapping("/team/profile/matching/private/{profileId}")
    @MemberOnly
    @CheckMatchingToPrivateProfileAccess
    public ResponseEntity<Void> createTeamProfileMatchingToPrivate(
            @Auth final Accessor accessor,
            @PathVariable final Long profileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {

        matchingService.createTeamProfileMatchingToPrivate(accessor.getMemberId(), profileId, matchingCreateRequest);
        return ResponseEntity.status(CREATED).build();
    }

    // 팀 소개서가 팀 소개서에 매칭 요청을 보내는 경우
    @PostMapping("/team/profile/matching/team/{teamMemberAnnouncementId}")
    @MemberOnly
    @CheckMatchingToTeamProfileAccess
    public ResponseEntity<Void> createTeamProfileMatchingToTeam(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        matchingService.createTeamProfileMatchingToTeam(accessor.getMemberId(), teamMemberAnnouncementId, matchingCreateRequest);
        return ResponseEntity.status(CREATED).build();
    }

    // 내 이력서가 팀 소개서에 매칭 요청을 보내는 경우
    @PostMapping("/private/profile/matching/team/{teamMemberAnnouncementId}")
    @MemberOnly
    @CheckMatchingToTeamProfileAccess
    public ResponseEntity<Void> createPrivateProfileMatchingToTeam(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        matchingService.createPrivateProfileMatchingToTeam(accessor.getMemberId(), teamMemberAnnouncementId, matchingCreateRequest);
        return ResponseEntity.status(CREATED).build();
    }

    // 내가 받은 매칭 조회
    @GetMapping("/matching/received")
    @MemberOnly
    public ResponseEntity<List<ReceivedMatchingResponse>> getReceivedMatchingResponses(
            @Auth final Accessor accessor
    ) {
        final List<ReceivedMatchingResponse> receivedMatchingResponseList = matchingService.getReceivedMatching(accessor.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(receivedMatchingResponseList);
    }

    // 내가 보낸 매칭 조회
    @GetMapping("/matching/request")
    @MemberOnly
    public ResponseEntity<List<RequestMatchingResponse>> getRequestMatchingResponses(
            @Auth final Accessor accessor
    ) {
        // accessor.getMemberId()가 보낸 모든 매칭 요청을 조회해야한다.
        final List<RequestMatchingResponse> requestMatchingResponseList = matchingService.getMyRequestMatching(accessor.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(requestMatchingResponseList);
    }

    // 성사된 매칭 조회
    @GetMapping("/matching/success")
    @MemberOnly
    public ResponseEntity<List<SuccessMatchingResponse>> getSuccessMatchingResponses(
            @Auth final Accessor accessor
    ) {
        // 내가 보낸 매칭 요청, 내가 받은 매칭 요청 중에서 성사된 모든 매칭을 조회해야한다.
        final List<SuccessMatchingResponse> successMatchingResponseList = matchingService.getMySuccessMatching(accessor.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(successMatchingResponseList);
    }

    // 내가 매칭 요청 보내기 전에 (어떤 프로필로 보낼지 선택하기 전에 응답 값 true/false)
    @GetMapping("/existence/profile")
    @MemberOnly
    public ResponseEntity<ExistenceProfileResponse> getExistenceProfileBoolean(
            @Auth final Accessor accessor
    ) {
        final ExistenceProfileResponse existenceProfileResponse = matchingService.getExistenceProfile(accessor.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(existenceProfileResponse);
    }




    // sender_type -> Private / receivedTeamProfile = false
    // 내가 받은 매칭 요청에서 private_matching 조회하는 경우 (개인 - 개인인 경우)
    @GetMapping("/received/private_to_private/matching/{privateMatchingId}")
    @MemberOnly
    public ResponseEntity<ReceivedPrivateMatchingMessageResponse> getReceivedPrivateToPrivateMatchingMessage(
            @Auth final Accessor accessor,
            @PathVariable final Long privateMatchingId
    ) {
        matchingService.validateReceivedMatchingRequest(accessor.getMemberId());
        final ReceivedPrivateMatchingMessageResponse receivedPrivateMatchingMessageResponse = matchingService.getReceivedPrivateToPrivateMatchingMessage(privateMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(receivedPrivateMatchingMessageResponse);
    }

    // sender_type -> Team / receivedTeamProfile = false
    // 내가 받은 매칭 요청에서 private_matching 조회하는 경우 (팀 - 개인인 경우)
    @GetMapping("/received/team_to_private/matching/{privateMatchingId}")
    @MemberOnly
    public ResponseEntity<ReceivedPrivateMatchingMessageResponse> getReceivedTeamToPrivateMatchingMessage(
            @Auth final Accessor accessor,
            @PathVariable final Long privateMatchingId
    ) {
        matchingService.validateReceivedMatchingRequest(accessor.getMemberId());
        final ReceivedPrivateMatchingMessageResponse receivedPrivateMatchingMessageResponse = matchingService.getReceivedTeamToPrivateMatchingMessage(privateMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(receivedPrivateMatchingMessageResponse);
    }

    // sender_type -> private / receivedTeamProfile = true
    // 내가 받은 매칭 요청에서 team_matching 조회하는 경우 (개인 - 팀인 경우)
    @GetMapping("/received/team_to_team/matching/{teamMatchingId}")
    @MemberOnly
    public ResponseEntity<ReceivedTeamMatchingMessageResponse> getReceivedPrivateToTeamMatchingResponse(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMatchingId
    ) {
        matchingService.validateReceivedMatchingRequest(accessor.getMemberId());
        final ReceivedTeamMatchingMessageResponse receivedTeamMatchingMessageResponse = matchingService.getReceivedPrivateToTeamMatchingMessage(teamMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(receivedTeamMatchingMessageResponse);
    }


    // sender_type -> private / requestTeamProfile = false
    // 내가 보낸 매칭 요청에서 private_matching 조회하는 경우 (개인 - 개인인 경우)
    @GetMapping("/request/private_to_private/matching/{privateMatchingId}")
    @MemberOnly
    public ResponseEntity<RequestPrivateMatchingMessageResponse> getRequestPrivateToPrivateMatchingMessage(
            @Auth final Accessor accessor,
            @PathVariable final Long privateMatchingId
    ) {
        final RequestPrivateMatchingMessageResponse requestPrivateMatchingMessageResponse = matchingService.getRequestPrivateToPrivateMatchingMessage(privateMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(requestPrivateMatchingMessageResponse);
    }


    // sender_type -> Team / requestTeamProfile = false
    // 내가 보낸 매칭 요청에서 private_matching 조회하는 경우 (팀 - 개인인 경우)
    @GetMapping("/request/team_to_private/matching/{privateMatchingId}")
    @MemberOnly
    public ResponseEntity<RequestPrivateMatchingMessageResponse> getRequestTeamToPrivateMatchingMessage(
            @Auth final Accessor accessor,
            @PathVariable final Long privateMatchingId
    ) {
        final RequestPrivateMatchingMessageResponse requestPrivateMatchingMessageResponse = matchingService.getRequestTeamToPrivateMatchingMessage(privateMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(requestPrivateMatchingMessageResponse);
    }


    // sender_type -> private / requestTeamProfile = true
    // 내가 보낸 매칭 요청에서 team_matching 조회하는 경우 (개인 - 팀인 경우)
    @GetMapping("/request/private_to_team/matching/{teamMatchingId}")
    @MemberOnly
    public ResponseEntity<RequestTeamMatchingMessageResponse> getRequestPrivateToTeamMatchingMessage(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMatchingId
    ) {
        final RequestTeamMatchingMessageResponse requestTeamMatchingMessageResponse = matchingService.getRequestPrivateToTeamMatchingMessage(teamMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(requestTeamMatchingMessageResponse);
    }



    // 내 이력서 관련 매칭일 때 수락/거절하기 버튼을 누른 경우
    @PostMapping("/allow/private/matching/{privateMatchingId}")
    @MemberOnly
    public ResponseEntity<Void> acceptReceivePrivateMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long privateMatchingId,
            @RequestBody @Valid final AllowMatchingRequest allowMatchingRequest
    ) {
        matchingService.acceptPrivateMatching(privateMatchingId, allowMatchingRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 팀 소개서 관련 매칭일 때 수락하기 버튼을 누른 경우
    @PostMapping("/allow/team/matching/{teamMatchingId}")
    @MemberOnly
    public ResponseEntity<Void> acceptReceiveTeamMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMatchingId,
            @RequestBody @Valid final AllowMatchingRequest allowMatchingRequest
    ) {
        matchingService.acceptTeamMatching(teamMatchingId, allowMatchingRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 내 이력서 관련 매칭일 때 연락하기 버튼을 누른 경우
    // 수신자가 내 이력서일 때
    @GetMapping("/success/private/matching/contact/{privateMatchingId}")
    @MemberOnly
    public ResponseEntity<SuccessContactResponse> getPrivateSuccessContactResponse(
            @Auth final Accessor accessor,
            @PathVariable final Long privateMatchingId
    ) {
        final SuccessContactResponse successContactResponse = matchingService.getPrivateSuccessContactResponse(accessor.getMemberId(), privateMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(successContactResponse);
    }

    // 팀 매칭 관련일 때 연락하기 버튼을 누른 경우
    // 수신자가 팀 소개서일 때
    @GetMapping("/success/team/matching/contact/{teamMatchingId}")
    @MemberOnly
    public ResponseEntity<SuccessContactResponse> getTeamSuccessContactResponse(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMatchingId
    ) {
        final SuccessContactResponse successContactResponse = matchingService.getTeamSuccessContactResponse(accessor.getMemberId(), teamMatchingId);
        return ResponseEntity.status(HttpStatus.OK).body(successContactResponse);
    }








    // 내가 보낸 매칭, 성사된 매칭에서 내 이력서 대상 매칭 삭제하기
    // matchingType -> PROFILE
    @DeleteMapping("/delete/private/matching/{privateMatchingId}")
    @MemberOnly
    public ResponseEntity<Void> deleteRequestPrivateMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long privateMatchingId
    ) {
        matchingService.deletePrivateMatching(privateMatchingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 내가 보낸 매칭, 성사된 매칭에서 팀 소개서 대상 매칭 삭제하기
    // matchingType -> TeamProfile
    @DeleteMapping("/delete/team/matching/{teamMatchingId}")
    @MemberOnly
    public ResponseEntity<Void> deleteRequestTeamMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMatchingId
    ) {
        matchingService.deleteTeamMatching(teamMatchingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
