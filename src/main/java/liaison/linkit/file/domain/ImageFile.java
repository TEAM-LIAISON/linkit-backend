package liaison.linkit.file.domain;

import static org.springframework.util.StringUtils.getFilenameExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import liaison.linkit.file.exception.image.EmptyImageRequestException;
import liaison.linkit.file.exception.image.ImageNameHashErrorException;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ImageFile {

    private static final String EXTENSION_DELIMITER = ".";

    private final MultipartFile file;
    private final String originalImageName;  // 원본 파일 이름
    private final String hashedName;         // 해시 처리된 저장용 이름

    public ImageFile(final MultipartFile file) {
        validateNullImage(file);
        this.file = file;
        this.originalImageName = resolveOriginalFilename(file);
        this.hashedName = hashName(file);
    }

    /**
     * 파일이 비었는지(0바이트) 여부 확인
     */
    private void validateNullImage(final MultipartFile file) {
        if (file.isEmpty()) {
            throw EmptyImageRequestException.EXCEPTION;
        }
    }

    /**
     * 원본 파일명(null이면 "unknown" 등 기본값 할당)
     */
    private String resolveOriginalFilename(MultipartFile image) {
        String name = image.getOriginalFilename();
        return (name != null) ? name : "unknown";
    }

    /**
     * 파일명을 해시화하여 저장용 이름 생성 ex) abc.jpg → <SHA3-256 해시>.jpg
     */
    private String hashName(final MultipartFile image) {
        // 1) 원본 파일명 안전 추출
        String name = resolveOriginalFilename(image);

        // 2) 확장자 추출
        String extension = getFilenameExtension(name);
        if (extension == null) {
            extension = "";
        } else {
            extension = EXTENSION_DELIMITER + extension;  // ".jpg" 등
        }

        // 3) 해시할 문자열 (원본파일명 + 현재시각 등)
        final String nameAndDate = name + LocalDateTime.now();

        // 4) SHA3-256 해시 생성 + 확장자 붙이기
        try {
            final MessageDigest hashAlgorithm = MessageDigest.getInstance("SHA3-256");
            final byte[] hashBytes = hashAlgorithm.digest(nameAndDate.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes) + extension;
        } catch (final NoSuchAlgorithmException e) {
            throw ImageNameHashErrorException.EXCEPTION;
        }
    }

    /**
     * 해시 바이트를 16진수 문자열로 변환
     */
    private String bytesToHex(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02x", bytes[i] & 0xff))
                .collect(Collectors.joining());
    }

    /**
     * 파일 MIME 타입
     */
    public String getContentType() {
        return this.file.getContentType();
    }

    /**
     * 파일 크기
     */
    public long getSize() {
        return this.file.getSize();
    }

    /**
     * 파일 스트림
     */
    public InputStream getInputStream() throws IOException {
        return this.file.getInputStream();
    }
}
