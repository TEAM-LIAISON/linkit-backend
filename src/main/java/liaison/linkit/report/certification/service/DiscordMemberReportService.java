package liaison.linkit.report.certification.service;

import liaison.linkit.global.external.discord.DiscordService;
import liaison.linkit.report.certification.dto.member.MemberCreateReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordMemberReportService implements MemberReportService {

    @Value("${discord.webhook.alert.member.url}")
    private String url;

    private final DiscordService discordService;

    @Override
    public void sendCreateMemberReport(MemberCreateReportDto memberCreateReportDto) {
        var msg = "## 회원 생성 알림"
            + "\n\n**회원 생성 ID:** " + memberCreateReportDto.memberId()
            + "\n**회원 생성 시간:** " + memberCreateReportDto.createdAt()
            + "\n**회원 이메일:** " + memberCreateReportDto.email() + "님이 회원가입하셨습니다.";

        discordService.sendMessages(url, msg);
    }
}
