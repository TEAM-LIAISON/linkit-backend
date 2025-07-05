package liaison.linkit.report.certification.service;

import liaison.linkit.report.certification.dto.chat.ChatMessageReportDto;

public interface ChatReportService {

    void sendChatMessageReport(ChatMessageReportDto chatMessageReportDto);
}
