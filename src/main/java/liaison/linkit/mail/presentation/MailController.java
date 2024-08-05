package liaison.linkit.mail.presentation;

import liaison.linkit.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("")
    public void mailConfirm(@RequestParam(name = "email") String email) throws Exception {
        String code = mailService.sendSimpleMessage(email);
        log.info("사용자에게 발송한 인증코드 ==> " + code);
    }
}
