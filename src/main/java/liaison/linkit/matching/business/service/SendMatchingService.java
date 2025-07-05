package liaison.linkit.matching.business.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jakarta.mail.MessagingException;

import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.mail.mapper.MatchingMailContentMapper;
import liaison.linkit.mail.service.AsyncMatchingEmailService;
import liaison.linkit.matching.business.assembler.SendMatchingModalAssembler;
import liaison.linkit.matching.business.mapper.MatchingMapper;
import liaison.linkit.matching.business.resolver.MatchingInfoResolver;
import liaison.linkit.matching.business.validator.MatchingValidator;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.MatchingEmailContent;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverAnnouncementInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToTeamMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.handler.NotificationHandler;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.HeaderNotificationService;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SendMatchingService {

    // Adapters
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final MatchingQueryAdapter matchingQueryAdapter;
    private final MatchingCommandAdapter matchingCommandAdapter;

    // Mappers
    private final MatchingMapper matchingMapper;
    private final MatchingMailContentMapper matchingMailContentMapper;

    // Validator
    private final MatchingValidator matchingValidator;

    // Resolver
    private final MatchingInfoResolver matchingInfoResolver;

    // Handler
    private final NotificationHandler notificationHandler;

    // Mail & Notification
    private final AsyncMatchingEmailService asyncMatchingEmailService;
    private final HeaderNotificationService headerNotificationService;

    // Assemblers
    private final SendMatchingModalAssembler sendMatchingModalAssembler;

    // 개인에게 매칭 요청 보낼 때 모달 정보 조회
    @Transactional(readOnly = true)
    public SelectMatchingRequestToProfileMenu selectMatchingRequestToProfileMenu(
            final Long senderMemberId, final String receiverEmailId) {
        return sendMatchingModalAssembler.assembleSelectMatchingRequestToProfileMenu(
                senderMemberId, receiverEmailId);
    }

    // 팀에게 매칭 요청 보낼 때 모달 정보 조회
    @Transactional(readOnly = true)
    public SelectMatchingRequestToTeamMenu selectMatchingRequestToTeamMenu(
            final Long senderMemberId, final String receiverTeamCode) {
        return sendMatchingModalAssembler.assembleSelectMatchingRequestToTeamMenu(
                senderMemberId, receiverTeamCode);
    }

    // ─── 매칭 발신함 삭제 처리 ─────────────────────────────────────────

    public DeleteRequestedMatchingItems deleteRequestedMatchingItems(
            final MatchingRequestDTO.DeleteRequestedMatchingRequest req) {
        List<Matching> matches = matchingQueryAdapter.findAllByIds(req.getMatchingIds());
        matches.forEach(matching -> matching.setSenderDeleteStatus(SenderDeleteStatus.DELETED));
        matchingCommandAdapter.updateAll(matches);
        List<DeleteRequestedMatchingItem> items =
                matchingMapper.toDeleteRequestedMatchingItemList(matches);
        return matchingMapper.toDeleteRequestedMatchingItems(items);
    }

    // ─── 매칭 요청 발신 ─────────────────────────────────────────────

    public MatchingResponseDTO.AddMatchingResponse addMatching(
            final Long memberId, final MatchingRequestDTO.AddMatchingRequest request)
            throws MessagingException, UnsupportedEncodingException {

        // 1. 유효성 검사 및 매칭 저장
        matchingValidator.validateAddMatching(memberId, request);
        Matching matching = matchingMapper.toMatching(request);
        Matching savedMatching = matchingCommandAdapter.addMatching(matching);

        Object senderInfo = matchingInfoResolver.resolveSenderInfo(matching);
        Object receiverInfo = matchingInfoResolver.resolveReceiverInfo(matching);

        // 4. 알림 처리
        processRequestedNotification(savedMatching);

        // 5. 이메일 전송
        sendMatchingRequestEmail(savedMatching);

        return matchingMapper.toAddMatchingResponse(
                matching,
                (senderInfo instanceof SenderProfileInformation
                        ? (SenderProfileInformation) senderInfo
                        : null),
                (senderInfo instanceof SenderTeamInformation
                        ? (SenderTeamInformation) senderInfo
                        : null),
                (receiverInfo instanceof ReceiverProfileInformation
                        ? (ReceiverProfileInformation) receiverInfo
                        : null),
                (receiverInfo instanceof ReceiverTeamInformation
                        ? (ReceiverTeamInformation) receiverInfo
                        : null),
                (receiverInfo instanceof ReceiverAnnouncementInformation
                        ? (ReceiverAnnouncementInformation) receiverInfo
                        : null));
    }

    // ─── 매칭 요청 발신함 메뉴 조회 ──────────────────────────────────────────

    public Page<RequestedMatchingMenu> getRequestedMatchingMenuResponse(
            final Long memberId, final SenderType senderType, Pageable pageable) {
        if (senderType != null) {
            return getMatchingPageBySenderType(memberId, senderType, pageable)
                    .map(this::toMatchingRequestedMenu);
        } else {
            List<Matching> combined = new ArrayList<>();
            combined.addAll(
                    getMatchingPageBySenderType(memberId, SenderType.PROFILE, pageable)
                            .getContent());
            combined.addAll(
                    getMatchingPageBySenderType(memberId, SenderType.TEAM, pageable).getContent());
            List<RequestedMatchingMenu> menus =
                    combined.stream().map(this::toMatchingRequestedMenu).toList();
            return new PageImpl<>(menus, pageable, menus.size());
        }
    }

    private Page<Matching> getMatchingPageBySenderType(
            final Long memberId, final SenderType senderType, Pageable pageable) {
        return switch (senderType) {
            case PROFILE -> {
                final String emailId = memberQueryAdapter.findEmailIdById(memberId);
                yield matchingQueryAdapter.findRequestedByProfile(emailId, pageable);
            }
            case TEAM -> {
                final List<Team> teams =
                        teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
                yield matchingQueryAdapter.findRequestedByTeam(teams, pageable);
            }
            default -> Page.empty(pageable);
        };
    }

    private RequestedMatchingMenu toMatchingRequestedMenu(final Matching matching) {
        // 채팅방 존재 여부 확인
        Long chatRoomId =
                chatRoomQueryAdapter.existsChatRoomByMatchingId(matching.getId())
                        ? chatRoomQueryAdapter.getChatRoomIdByMatchingId(matching.getId())
                        : null;

        Object senderInfo = matchingInfoResolver.resolveSenderInfo(matching);
        Object receiverInfo = matchingInfoResolver.resolveReceiverInfo(matching);

        return matchingMapper.toMatchingRequestedMenu(
                matching,
                chatRoomId,
                (senderInfo instanceof SenderProfileInformation
                        ? (SenderProfileInformation) senderInfo
                        : null),
                (senderInfo instanceof SenderTeamInformation
                        ? (SenderTeamInformation) senderInfo
                        : null),
                (receiverInfo instanceof ReceiverProfileInformation
                        ? (ReceiverProfileInformation) receiverInfo
                        : null),
                (receiverInfo instanceof ReceiverTeamInformation
                        ? (ReceiverTeamInformation) receiverInfo
                        : null),
                (receiverInfo instanceof ReceiverAnnouncementInformation
                        ? (ReceiverAnnouncementInformation) receiverInfo
                        : null));
    }

    // ─── 매칭 요청 발신용 이메일 전송 ───────────────────────────────────────────

    private void sendMatchingRequestEmail(final Matching matching)
            throws MessagingException, UnsupportedEncodingException {
        MatchingEmailContent emailContent =
                matchingMailContentMapper.generateMatchingRequestedEmailContent(matching);

        asyncMatchingEmailService.sendMatchingRequestedEmail(
                emailContent.getReceiverMailTitle(),
                emailContent.getReceiverMailSubTitle(),
                emailContent.getReceiverMailSubText(),
                matchingInfoResolver.getReceiverEmail(matching),
                matchingInfoResolver.getSenderName(matching),
                matchingInfoResolver.getSenderLogoImagePath(matching),
                matchingInfoResolver.getSenderPositionOrTeamSizeText(matching),
                matchingInfoResolver.getSenderPositionOrTeamSize(matching),
                matchingInfoResolver.getSenderRegionDetail(matching));
    }

    // ─── 알림 처리 (매칭 요청 상태) ─────────────────────────────────────────

    private void processRequestedNotification(final Matching savedMatching) {
        NotificationDetails notificationDetails =
                notificationHandler.generateRequestedStateReceiverNotificationDetails(
                        savedMatching);
        notificationHandler.alertNewRequestedNotificationToReceiver(
                savedMatching, notificationDetails);
        //        headerNotificationService.publishNotificationCount(
        //                matchingInfoResolver.getReceiverMemberId(savedMatching));
    }
}
