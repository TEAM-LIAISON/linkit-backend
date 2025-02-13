package liaison.linkit.report.certification.service;

import liaison.linkit.global.external.discord.DiscordService;
import liaison.linkit.report.certification.dto.ProfileLicenseCertificationReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordProfileCertificationReportService implements ProfileCertificationReportService {

    @Value("${discord.webhook.alert.certification.url}")
    private String url;

    private final DiscordService discordService;
    private static final String TITLE = "프로필 증명서 알림";

    @Override
    public void sendProfileLicenseReport(ProfileLicenseCertificationReportDto profileLicenseCertificationReportDto) {
        var msg = TITLE
            + "\n*프로필 자격증 ID:* " + profileLicenseCertificationReportDto.profileLicenseId()
            + "\n*회원 이름:* " + profileLicenseCertificationReportDto.memberName()
            + "\n*프로필 자격증명:* " + profileLicenseCertificationReportDto.licenseName()
            + "\n*업로드 시간:* " + profileLicenseCertificationReportDto.uploadTime();

        discordService.sendMessages(url, msg);
    }
}
