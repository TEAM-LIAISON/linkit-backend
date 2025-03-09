package liaison.linkit.mail.presentation.dto;

import java.util.List;

import liaison.linkit.profile.domain.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 모집 공고 광고 이메일 발송을 위한 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementAdvertiseDTO {

    // 공고 ID
    private Long announcementId;

    // 공고 제목
    private String announcementTitle;

    // 팀 이름
    private String teamName;

    // 팀 코드
    private String teamCode;

    // 팀 로고 이미지 경로
    private String teamLogoImagePath;

    // 포지션 대분류
    private String majorPosition;

    // 포지션 소분류
    private String subPosition;

    // 스킬 목록
    private List<String> skillNames;

    // 배치 ID (배치 실행 단위 식별용)
    private String batchId;

    // 이메일 수신자 목록
    private List<Profile> receivers;
}
