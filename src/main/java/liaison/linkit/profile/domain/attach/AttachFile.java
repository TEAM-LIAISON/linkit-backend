//package liaison.linkit.profile.domain.attach;
//
//import jakarta.persistence.*;
//import liaison.linkit.profile.domain.Profile;
//import liaison.linkit.profile.dto.request.attach.AttachFileUpdateRequest;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import static jakarta.persistence.CascadeType.ALL;
//import static jakarta.persistence.FetchType.LAZY;
//
//@Entity
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name = "attach_file")
//public class AttachFile {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "attach_file_id")
//    private Long id;
//
//    @ManyToOne(fetch = LAZY, cascade = ALL)
//    @JoinColumn(name = "profile_id")
//    private Profile profile;
//
//    @Column(nullable = false)
//    private String attachFileName;
//
//    @Column(nullable = false)
//    private String attachFilePath;
//
//    public static AttachFile of(
//            final Profile profile,
//            final String attachFileName,
//            final String attachFilePath
//    ) {
//        return new AttachFile(
//                null,
//                profile,
//                attachFileName,
//                attachFilePath
//        );
//    }
//
//    public void update(final AttachFileUpdateRequest updateRequest) {
//        this.attachFilePath = updateRequest.getAttachFilePath();
//    }
//}
