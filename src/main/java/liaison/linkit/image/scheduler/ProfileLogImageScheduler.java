package liaison.linkit.image.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.profile.domain.ProfileLogImage;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileLogImageScheduler {

    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileLogCommandAdapter profileLogCommandAdapter;

    private final S3Uploader s3Uploader;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    public void cleanUpUnusedImages() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1); // 1일 지난 이미지
        List<ProfileLogImage> unusedProfileLogImages = profileLogQueryAdapter.getUnusedProfileLogImages(threshold);
        for (ProfileLogImage profileLogImage : unusedProfileLogImages) {
            // S3에서 이미지 삭제
            s3Uploader.deleteS3Image(profileLogImage.getProfileLogImageUrl());

            // 데이터베이스에서 엔티티 삭제
            profileLogCommandAdapter.removeProfileLogImage(profileLogImage);
        }
    }
}
