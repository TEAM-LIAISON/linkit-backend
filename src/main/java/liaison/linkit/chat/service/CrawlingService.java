package liaison.linkit.chat.service;

import liaison.linkit.chat.domain.CrawlingInfo;
import liaison.linkit.chat.domain.repository.CrawlingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CrawlingService {
    private final CrawlingRepository crawlingRepository;

    public CrawlingInfo getCrawlingInfoByName(String name) {
        log.info("Fetching CrawlingInfo for name: {}", name);
        CrawlingInfo info = crawlingRepository.findByName(name);
        if (info == null) {
            log.warn("No CrawlingInfo found for name: {}", name);
        }
        return info;
    }
}
