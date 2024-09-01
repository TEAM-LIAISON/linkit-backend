package liaison.linkit.mail.presentation;

import liaison.linkit.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

//    // 1. 내 이력서 -> 내 이력서로 매칭 요청 보낸 경우
//    @PostMapping("/private/private")
//    public ResponseEntity<?> sendPrivateToPrivateMatchingRequest() throws Exception {
//        mailService.mailPrivateToPrivate(
//                "kwondm7@naver.com",
//                "권동민",
//                "주서영",
//                LocalDateTime.now(),
//                "요청 메시지입니다."
//        );
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    // 2. 팀 소개서 -> 내 이력서로 매칭 요청 보낸 경우
//    @PostMapping("/team/private")
//    public ResponseEntity<?> sendTeamToPrivateMatchingRequest() throws Exception {
//        mailService.mailTeamToPrivate(
//                "kwondm7@naver.com",
//                "권동민",
//                "리에종",
//                LocalDateTime.now(),
//                "요청 메시지입니다."
//        );
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    // 3. 내 이력서 -> 팀 소개서로 매칭 요청 보낸 경우
//    @PostMapping("/private/team")
//    public ResponseEntity<?> sendPrivateToTeamMatchingRequest() throws Exception {
//        mailService.mailPrivateToTeam(
//                "kwondm7@naver.com",
//                "리에종",
//                "권동민",
//                LocalDateTime.now(),
//                "요청 메시지입니다."
//        );
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    // 4. 팀 소개서 -> 팀 소개서로 매칭 요청 보낸 경우
//    @PostMapping("/team/team")
//    public ResponseEntity<?> sendTeamToTeamMatchingRequest() throws Exception {
//        mailService.mailTeamToTeam(
//                "kwondm7@naver.com",
//                "리에종",
//                "권동민",
//                LocalDateTime.now(),
//                "요청 메시지입니다."
//        );
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
}
