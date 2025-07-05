package liaison.linkit.image.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.image.domain.Image;
import liaison.linkit.image.implement.ImageCommandAdapter;
import liaison.linkit.image.implement.ImageQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageScheduler {

    private final ImageQueryAdapter imageQueryAdapter;
    private final ImageCommandAdapter imageCommandAdapter;

    private final S3Uploader s3Uploader;

    @Scheduled(cron = "0 0 5 * * ?", zone = "Asia/Seoul")
    public void cleanUpUnusedImages() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1); // 1일 지난 이미지
        List<Image> unusedImages = imageQueryAdapter.getUnusedImages(threshold);
        for (Image image : unusedImages) {
            // S3에서 이미지 삭제
            s3Uploader.deleteS3Image(image.getImageUrl());

            // 데이터베이스에서 엔티티 삭제
            imageCommandAdapter.removeImage(image);
        }
    }
}
