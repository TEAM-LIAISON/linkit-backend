package liaison.linkit.image.presentation;

import liaison.linkit.image.dto.MiniProfileImageResponse;
import liaison.linkit.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/mini-profile")
    public ResponseEntity<MiniProfileImageResponse> uploadMiniProfileImage(
            @RequestPart MultipartFile miniProfileImage
    ) {
        final MiniProfileImageResponse miniProfileImageResponse = imageService.save(miniProfileImage);
//        final String imageName = miniProfileImageResponse.getImageName();
        return ResponseEntity.status(HttpStatus.CREATED).body(miniProfileImageResponse);
    }
}
