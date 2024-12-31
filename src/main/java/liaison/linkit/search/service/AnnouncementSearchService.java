package liaison.linkit.search.service;

import java.util.List;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AnnouncementSearchService {

    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;

    public Page<AnnouncementInform> searchAnnouncementsInLoginState(
            final Long memberId,
            List<String> majorPosition,
            List<String> skillName,
            List<String> cityName,
            List<String> scaleName,
            Pageable pageable
    ) {
        Page<TeamMemberAnnouncement> announcements = teamMemberAnnouncementQueryAdapter.findAll(majorPosition, skillName, cityName, scaleName, pageable);
        return announcements.map(
                teamMemberAnnouncement -> toSearchAnnouncementInformInLoginState(memberId, teamMemberAnnouncement)
        );
    }

}
