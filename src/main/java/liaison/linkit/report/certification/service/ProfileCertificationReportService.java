package liaison.linkit.report.certification.service;

import liaison.linkit.report.certification.dto.activity.ProfileActivityCertificationReportDto;
import liaison.linkit.report.certification.dto.awards.ProfileAwardsCertificationReportDto;
import liaison.linkit.report.certification.dto.education.ProfileEducationCertificationReportDto;
import liaison.linkit.report.certification.dto.license.ProfileLicenseCertificationReportDto;

public interface ProfileCertificationReportService {

    void sendProfileActivityReport(ProfileActivityCertificationReportDto profileActivityCertificationReportDto);

    void sendProfileEducationReport(ProfileEducationCertificationReportDto profileEducationCertificationReportDto);

    void sendProfileAwardsReport(ProfileAwardsCertificationReportDto profileAwardsCertificationReportDto);

    void sendProfileLicenseReport(ProfileLicenseCertificationReportDto profileLicenseCertificationReportDto);
}
