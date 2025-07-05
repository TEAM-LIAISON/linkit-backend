package liaison.linkit.report.certification.dto.chat;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ChatMessageReportDto(
        String chatMessageId,
        String content,
        LocalDateTime timestamp,
        String chatMessageSenderEmail,
        String chatMessageReceiverEmail) {}
