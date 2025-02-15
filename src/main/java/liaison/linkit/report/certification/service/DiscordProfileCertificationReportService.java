package liaison.linkit.report.certification.service;

import liaison.linkit.global.external.discord.DiscordService;
import liaison.linkit.report.certification.dto.profile.activity.ProfileActivityCertificationReportDto;
import liaison.linkit.report.certification.dto.profile.awards.ProfileAwardsCertificationReportDto;
import liaison.linkit.report.certification.dto.profile.education.ProfileEducationCertificationReportDto;
import liaison.linkit.report.certification.dto.profile.license.ProfileLicenseCertificationReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordProfileCertificationReportService implements ProfileCertificationReportService {

    @Value("${discord.webhook.alert.certification.url}")
    private String url;

    private final DiscordService discordService;

    @Override
    public void sendProfileActivityReport(ProfileActivityCertificationReportDto profileActivityCertificationReportDto) {
        var msg = "## 프로필 이력 알림"
            + "\n\n**프로필 이력 ID:** " + profileActivityCertificationReportDto.profileActivityId()
            + "\n**유저 아이디:** " + profileActivityCertificationReportDto.emailId()
            + "\n**프로필 이력 활동명:** " + profileActivityCertificationReportDto.activityName()
            + "\n**업로드 시간:** " + profileActivityCertificationReportDto.uploadTime()

            + "\n\n**첨부 파일 이름:** " + profileActivityCertificationReportDto.activityCertificationAttachFileName()
            + "\n**첨부 파일 경로:** " + profileActivityCertificationReportDto.activityCertificationAttachFilePath();

        discordService.sendMessages(url, msg);
    }

    @Override
    public void sendProfileEducationReport(ProfileEducationCertificationReportDto profileEducationCertificationReportDto) {
        var msg = "## 프로필 학력 알림"
            + "\n\n**프로필 학력 ID:** " + profileEducationCertificationReportDto.profileEducationId()
            + "\n**유저 아이디:** " + profileEducationCertificationReportDto.emailId()
            + "\n**프로필 학력 학교명:** " + profileEducationCertificationReportDto.universityName()
            + "\n**업로드 시간:** " + profileEducationCertificationReportDto.uploadTime()

            + "\n\n**첨부 파일 이름:** " + profileEducationCertificationReportDto.educationCertificationAttachFileName()
            + "\n**첨부 파일 경로:** " + profileEducationCertificationReportDto.educationCertificationAttachFilePath();

        discordService.sendMessages(url, msg);
    }

    @Override
    public void sendProfileAwardsReport(ProfileAwardsCertificationReportDto profileAwardsCertificationReportDto) {
        var msg = "## 프로필 수상 알림"
            + "\n\n**프로필 수상 ID:** " + profileAwardsCertificationReportDto.profileAwardsId()
            + "\n**유저 아이디:** " + profileAwardsCertificationReportDto.emailId()
            + "\n**프로필 수상 대회명:** " + profileAwardsCertificationReportDto.awardsName()
            + "\n**업로드 시간:** " + profileAwardsCertificationReportDto.uploadTime()

            + "\n\n**첨부 파일 이름:** " + profileAwardsCertificationReportDto.awardsCertificationAttachFileName()
            + "\n**첨부 파일 경로:** " + profileAwardsCertificationReportDto.awardsCertificationAttachFilePath();

        discordService.sendMessages(url, msg);
    }

    @Override
    public void sendProfileLicenseReport(ProfileLicenseCertificationReportDto profileLicenseCertificationReportDto) {
        var msg = "## 프로필 증명서 알림"
            + "\n\n**프로필 자격증 ID:** " + profileLicenseCertificationReportDto.profileLicenseId()
            + "\n**유저 아이디:** " + profileLicenseCertificationReportDto.emailId()
            + "\n**프로필 자격증 이름:** " + profileLicenseCertificationReportDto.licenseName()
            + "\n**업로드 시간:** " + profileLicenseCertificationReportDto.uploadTime()

            + "\n\n**첨부 파일 이름:** " + profileLicenseCertificationReportDto.licenseCertificationAttachFileName()
            + "\n**첨부 파일 경로:** " + profileLicenseCertificationReportDto.licenseCertificationAttachFilePath();

        discordService.sendMessages(url, msg);
    }
}
