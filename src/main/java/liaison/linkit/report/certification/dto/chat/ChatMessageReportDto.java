package liaison.linkit.report.certification.dto.chat;

import java.time.LocalDateTime;

public record ChatMessageReportDto(
    String chatMessageId,
    String content,
    LocalDateTime timestamp
) {

}
