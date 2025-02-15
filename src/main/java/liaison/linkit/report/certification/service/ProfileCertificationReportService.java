package liaison.linkit.report.certification.service;

import liaison.linkit.report.certification.dto.profile.activity.ProfileActivityCertificationReportDto;
import liaison.linkit.report.certification.dto.profile.awards.ProfileAwardsCertificationReportDto;
import liaison.linkit.report.certification.dto.profile.education.ProfileEducationCertificationReportDto;
import liaison.linkit.report.certification.dto.profile.license.ProfileLicenseCertificationReportDto;

public interface ProfileCertificationReportService {

    void sendProfileActivityReport(ProfileActivityCertificationReportDto profileActivityCertificationReportDto);

    void sendProfileEducationReport(ProfileEducationCertificationReportDto profileEducationCertificationReportDto);

    void sendProfileAwardsReport(ProfileAwardsCertificationReportDto profileAwardsCertificationReportDto);

    void sendProfileLicenseReport(ProfileLicenseCertificationReportDto profileLicenseCertificationReportDto);
}
