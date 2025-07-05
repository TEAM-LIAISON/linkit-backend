package liaison.linkit.report.certification.service;

import liaison.linkit.global.external.discord.DiscordService;
import liaison.linkit.report.certification.dto.chat.ChatMessageReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordChatReportService implements ChatReportService {

    @Value("${discord.webhook.alert.chat.url}")
    private String url;

    private final DiscordService discordService;

    @Override
    public void sendChatMessageReport(ChatMessageReportDto chatMessageReportDto) {
        var msg =
                "## 채팅 메시지 알림"
                        + "\n\n**채팅 메시지 ID:** "
                        + chatMessageReportDto.chatMessageId()
                        + "\n**채팅 메시지 내용:** "
                        + chatMessageReportDto.content()
                        + "\n**메시지 전송 시간:** "
                        + chatMessageReportDto.timestamp()
                        + "\n\n**메시지 발신자 이메일:** "
                        + chatMessageReportDto.chatMessageSenderEmail()
                        + "\n**메시지 수신자 이메일:** "
                        + chatMessageReportDto.chatMessageReceiverEmail();

        discordService.sendMessages(url, msg);
    }
}
