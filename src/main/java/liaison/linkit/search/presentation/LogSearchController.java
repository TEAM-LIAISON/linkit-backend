package liaison.linkit.search.presentation;

import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.search.business.service.LogSearchService;
import liaison.linkit.search.presentation.dto.LogResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class LogSearchController {

    private final LogSearchService logSearchService;

    @GetMapping("/home/logs")
    public CommonResponse<LogResponseDTO.LogInformMenus> getHomeLogInformMenus() {
        return CommonResponse.onSuccess(logSearchService.getHomeLogInformMenus());
    }
}
