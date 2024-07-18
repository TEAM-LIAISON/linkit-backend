package liaison.linkit.matching.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.matching.CheckMatchingToPrivateProfileAccess;
import liaison.linkit.matching.CheckMatchingToTeamProfileAccess;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.dto.response.ReceivedMatchingResponse;
import liaison.linkit.matching.dto.response.RequestMatchingResponse;
import liaison.linkit.matching.dto.response.SuccessMatchingResponse;
import liaison.linkit.matching.service.MatchingService;
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
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 팀 소개서가 팀 소개서에 매칭 요청을 보내는 경우
    @PostMapping("/team/profile/matching/team/{teamProfileId}")
    @MemberOnly
    @CheckMatchingToTeamProfileAccess
    public ResponseEntity<Void> createTeamProfileMatchingToTeam(
            @Auth final Accessor accessor,
            @PathVariable final Long teamProfileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        matchingService.createTeamProfileMatchingToTeam(accessor.getMemberId(), teamProfileId, matchingCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 내 이력서가 팀 소개서에 매칭 요청을 보내는 경우
    @PostMapping("/private/profile/matching/team/{teamProfileId}")
    @MemberOnly
    @CheckMatchingToTeamProfileAccess
    public ResponseEntity<Void> createPrivateProfileMatchingToTeam(
            @Auth final Accessor accessor,
            @PathVariable final Long teamProfileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        matchingService.createPrivateProfileMatchingToTeam(accessor.getMemberId(), teamProfileId, matchingCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

//    @GetMapping("/matching/received/{profileId}")
//    @MemberOnly
//    public ResponseEntity<MatchingMessageResponse> getMatchingMessageResponse(
//            @Auth final Accessor accessor,
//            @PathVariable final Long profileId
//    ) {
//        final MatchingMessageResponse matchingMessageResponse = matchingService.getMatchingMessageResponse(accessor.getMemberId(), profileId);
//        return ResponseEntity.status(HttpStatus.OK).body(matchingMessageResponse);
//    }
}
