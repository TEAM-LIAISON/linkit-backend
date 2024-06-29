package liaison.linkit.search.presentation;

import liaison.linkit.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
// 팀 찾기 및 팀원 찾기 컨트롤러
public class SearchController {

    public final SearchService searchService;

    // 팀 찾기 구현부

    // 팀원 찾기 구현부

}
