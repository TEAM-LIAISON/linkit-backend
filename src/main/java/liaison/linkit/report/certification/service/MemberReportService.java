package liaison.linkit.report.certification.service;

import liaison.linkit.report.certification.dto.member.MemberCreateReportDto;

public interface MemberReportService {

    void sendCreateMemberReport(MemberCreateReportDto memberCreateReportDto);
}
