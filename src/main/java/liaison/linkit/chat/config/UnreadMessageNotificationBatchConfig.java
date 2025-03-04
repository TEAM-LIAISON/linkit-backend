package liaison.linkit.chat.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatNotificationLog;
import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.chat.domain.repository.chatNotificationLog.ChatNotificationLogRepository;
import liaison.linkit.chat.presentation.dto.UnreadMessageNotificationDTO;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UnreadMessageNotificationBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberQueryAdapter memberQueryAdapter;
    private final ChatNotificationLogRepository notificationLogRepository;

    @Bean
    public Job unreadMessageNotificationJob() {
        return new JobBuilder("unreadMessageNotificationJob", jobRepository)
                .start(unreadMessageNotificationStep())
                .build();
    }

    @Bean
    public Step unreadMessageNotificationStep() {
        return new StepBuilder("unreadMessageNotificationStep", jobRepository)
                .<ChatMessage, UnreadMessageNotificationDTO>chunk(10, transactionManager)
                .reader(unreadMessageReader())
                .processor(unreadMessageProcessor())
                .writer(unreadMessageNotificationWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemReader<ChatMessage> unreadMessageReader() {
        // 1시간 전에 전송된 읽지 않은 메시지 조회
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        List<ChatMessage> unreadMessages =
                chatMessageRepository.findUnreadMessagesOlderThan(thirtyMinutesAgo);

        log.info("Unread messages: " + unreadMessages.size());

        // 이미 알림을 보낸 메시지는 필터링
        List<ChatMessage> messagesToNotify = new ArrayList<>();
        for (ChatMessage message : unreadMessages) {
            if (message != null && message.getId() != null) {
                List<ChatNotificationLog> logs =
                        notificationLogRepository.findByChatMessageId(message.getId());
                log.info("Notification logs: " + logs.size());
                if (logs.isEmpty()) {
                    messagesToNotify.add(message);
                    log.info("Message to notify: " + message.getId());
                }
            }
        }

        return new ListItemReader<>(messagesToNotify);
    }

    @Bean
    public ItemProcessor<ChatMessage, UnreadMessageNotificationDTO> unreadMessageProcessor() {
        return message -> {
            // messageReceiverMemberId로 수신자 정보 조회
            Member receiver = memberQueryAdapter.findById(message.getMessageReceiverMemberId());

            if (receiver != null && receiver.getEmail() != null) {
                UnreadMessageNotificationDTO dto =
                        UnreadMessageNotificationDTO.builder()
                                .messageId(message.getId())
                                .chatRoomId(message.getChatRoomId())
                                .senderName(message.getMessageSenderName())
                                .senderType(
                                        message.getMessageSenderType() != null
                                                ? message.getMessageSenderType().toString()
                                                : null)
                                .receiverEmail(receiver.getEmail())
                                .receiverMemberId(message.getMessageReceiverMemberId())
                                .build();

                // 메시지 미리보기 생성 (예: 첫 50자)
                String preview = message.getContent();
                if (preview.length() > 50) {
                    preview = preview.substring(0, 47) + "...";
                }
                dto.setMessagePreview(preview);

                return dto;
            }

            return null;
        };
    }

    @Bean
    public ItemWriter<UnreadMessageNotificationDTO> unreadMessageNotificationWriter() {
        return items -> {
            List<ChatNotificationLog> logs = new ArrayList<>();

            for (UnreadMessageNotificationDTO item : items) {
                if (item != null) {
                    try {
                        // 알림 기록 저장
                        ChatNotificationLog log =
                                ChatNotificationLog.builder()
                                        .chatMessageId(item.getMessageId())
                                        .chatRoomId(item.getChatRoomId())
                                        .receiverMemberId(item.getReceiverMemberId())
                                        .sentAt(LocalDateTime.now())
                                        .build();

                        logs.add(log);
                    } catch (Exception e) {
                        // 로깅 등의 예외 처리
                        System.err.println("Error sending notification: " + e.getMessage());
                    }
                }
            }

            // 알림 기록 저장
            if (!logs.isEmpty()) {
                notificationLogRepository.saveAll(logs);
            }
        };
    }
}
