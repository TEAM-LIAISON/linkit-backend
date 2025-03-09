package liaison.linkit.mail.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import jakarta.mail.MessagingException;

import org.springframework.scheduling.annotation.Async;

public interface AsyncAnnouncementAdvertiseEmailService {
    // 모집 공고 광고성 이메일 발송
    @Async("announcementTaskExecutor")
    void sendAnnouncementAdvertiseEmail(
            final String receiverMailAddress,
            final String announcementMajorPositionName,
            final String announcementMinorPositionName, // 추가: 포지션 소분류
            final String teamCode,
            final String teamLogoImagePath,
            final String teamName,
            final String announcementTitle,
            final List<String> announcementSkillNames,
            final Long announcementId)
            throws MessagingException, UnsupportedEncodingException;
}
