package liaison.linkit.chat.presentation;

import liaison.linkit.chat.domain.CrawlingInfo;
import liaison.linkit.chat.service.CrawlingService;
import liaison.linkit.global.presentation.dto.Message;
import liaison.linkit.global.presentation.dto.StatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CrawlingController {

    private final CrawlingService crawlingService;

    @PostMapping("/crawl")
    public ResponseEntity<Message> crawl(@RequestParam String title) {
        Message message = new Message();

        try {
            CrawlingInfo crawlingInfo = crawlingService.getCrawlingInfoByName(title);

            if (crawlingInfo != null) {
                message.setStatus(StatusEnum.OK);
                message.setMessage("해당하는 이름을 가져왔습니다.");
                message.setData(crawlingInfo);
            } else {
                message.setStatus(StatusEnum.NOT_FOUND);
                message.setMessage("해당하는 이름을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("Error while fetching crawling info for title: {}", title, e);
            message.setStatus(StatusEnum.ERROR);
            message.setMessage("데이터를 가져오는 중 오류가 발생했습니다.");
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/test-mongo")
    public ResponseEntity<Message> testMongo(@RequestParam String name) {
        Message message = new Message();

        try {
            CrawlingInfo info = crawlingService.getCrawlingInfoByName(name);

            if (info != null) {
                message.setStatus(StatusEnum.OK);
                message.setMessage("데이터를 성공적으로 가져왔습니다.");
                message.setData(info);
            } else {
                message.setStatus(StatusEnum.NOT_FOUND);
                message.setMessage("해당 이름의 데이터를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("Error while retrieving data for name: {}", name, e);
            message.setStatus(StatusEnum.ERROR);
            message.setMessage("데이터 가져오기 중 오류가 발생했습니다.");
        }

        return ResponseEntity.status(message.getStatus() == StatusEnum.OK ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(message);
    }
}
