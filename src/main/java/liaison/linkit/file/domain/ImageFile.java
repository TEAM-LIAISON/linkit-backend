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
    private final String hashedName;

    public ImageFile(final MultipartFile file) {
        validateNullImage(file);
        this.file = file;
        this.hashedName = hashName(file);
    }

    private void validateNullImage(final MultipartFile file) {
        if (file.isEmpty()) {
            throw EmptyImageRequestException.EXCEPTION;
        }
    }

    private String hashName(final MultipartFile image) {
        final String name = image.getOriginalFilename();
        final String filenameExtension = EXTENSION_DELIMITER + getFilenameExtension(name);
        final String nameAndDate = name + LocalDateTime.now();
        try {
            final MessageDigest hashAlgorithm = MessageDigest.getInstance("SHA3-256");
            final byte[] hashBytes = hashAlgorithm.digest(nameAndDate.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes) + filenameExtension;
        } catch (final NoSuchAlgorithmException e) {
            throw ImageNameHashErrorException.EXCEPTION;
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
