package liaison.linkit.notification.service;

import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
//    private final MailSender mailSender;


}
