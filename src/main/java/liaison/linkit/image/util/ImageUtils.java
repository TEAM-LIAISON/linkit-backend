package liaison.linkit.image.util;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageUtils {

    public List<String> extractImageUrls(String htmlContent) {
        List<String> imageUrls = new ArrayList<>();
        Document document = Jsoup.parse(htmlContent);
        Elements images = document.select("img");
        for (Element img : images) {
            String src = img.attr("src");
            imageUrls.add(src);
        }
        return imageUrls;
    }
}
