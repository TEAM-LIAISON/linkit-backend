package liaison.linkit.report.certification.dto.member;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record MemberCreateReportDto(Long memberId, String email, LocalDateTime createdAt) {}
