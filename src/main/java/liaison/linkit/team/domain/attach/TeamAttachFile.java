//package liaison.linkit.team.domain.attach;
//
//import jakarta.persistence.*;
//import liaison.linkit.team.domain.TeamProfile;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import static jakarta.persistence.CascadeType.ALL;
//import static jakarta.persistence.FetchType.LAZY;
//import static jakarta.persistence.GenerationType.IDENTITY;
//
//@Entity
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name = "team_attach_file")
//public class TeamAttachFile {
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    @Column(name = "team_attach_file_id")
//    private Long id;
//
//    @ManyToOne(fetch = LAZY, cascade = ALL)
//    @JoinColumn(name = "team_profile_id")
//    private TeamProfile teamProfile;
//
//    @Column(nullable = false)
//    private String teamAttachFileName;
//
//    @Column(nullable = false)
//    private String teamAttachFilePath;
//
//    public static TeamAttachFile of(
//            final TeamProfile teamProfile,
//            final String teamAttachFileName,
//            final String teamAttachFilePath
//    ) {
//        return new TeamAttachFile(
//                null,
//                teamProfile,
//                teamAttachFileName,
//                teamAttachFilePath
//        );
//    }
//}
