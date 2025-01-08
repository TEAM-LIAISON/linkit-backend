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
import liaison.linkit.file.exception.file.EmptyFileRequestException;
import liaison.linkit.file.exception.file.FileNameHashErrorException;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CertificationFile {

    private static final String EXTENSION_DELIMITER = ".";

    private final MultipartFile file;
    private final String originalFileName;
    private final String hashedName;

    public CertificationFile(final MultipartFile file) {
        validateNullFile(file);
        this.file = file;
        this.originalFileName = file.getOriginalFilename();
        this.hashedName = hashName(file);
    }

    private void validateNullFile(final MultipartFile file) {
        if (file.isEmpty()) {
            throw EmptyFileRequestException.EXCEPTION;
        }
    }

    /**
     * file.getOriginalFilename()가 null이 아닌지 체크 후 반환
     */
    private String resolveOriginalFilename(MultipartFile file) {
        String name = file.getOriginalFilename();
        return (name != null) ? name : "unknown"; // 혹은 "", "unknown", 등등
    }

    /**
     * 파일명을 해시화하여 저장용 이름 생성 ex) abc.txt -> <SHA3-256 해시>.txt
     */
    private String hashName(final MultipartFile attachFile) {
        // 1) 원본 파일명
        String name = resolveOriginalFilename(attachFile);

        // 2) 확장자 추출
        String extension = getFilenameExtension(name);
        if (extension == null) {
            extension = "";
        } else {
            extension = EXTENSION_DELIMITER + extension; //  ".txt" 형태
        }

        // 3) 해시할 문자열 (원본파일명 + 현재시각 등)
        final String nameAndDate = name + LocalDateTime.now();

        try {
            final MessageDigest hashAlgorithm = MessageDigest.getInstance("SHA3-256");
            final byte[] hashBytes = hashAlgorithm.digest(nameAndDate.getBytes(StandardCharsets.UTF_8));
            // SHA3-256 해시를 Hex 문자열로 변환 + 확장자 붙이기
            return bytesToHex(hashBytes) + extension;
        } catch (NoSuchAlgorithmException e) {
            throw FileNameHashErrorException.EXCEPTION;
        }
    }

    private String bytesToHex(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02x", bytes[i] & 0xff))
                .collect(Collectors.joining());
    }

    public String getContentType() {
        return this.file.getContentType();
    }

    public long getSize() {
        return this.file.getSize();
    }

    public InputStream getInputStream() throws IOException {
        return this.file.getInputStream();
    }
}
