package liaison.linkit.search.business.service;

import java.util.List;
import java.util.Optional;
import liaison.linkit.team.business.assembler.AnnouncementInformMenuAssembler;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
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
    private final AnnouncementInformMenuAssembler announcementInformMenuAssembler;
    
    public Page<AnnouncementInformMenu> searchAnnouncements(
        final Optional<Long> optionalMemberId,
        List<String> majorPosition,
        List<String> skillName,
        List<String> cityName,
        List<String> scaleName,
        Pageable pageable
    ) {
        Page<TeamMemberAnnouncement> announcements = teamMemberAnnouncementQueryAdapter.findAll(majorPosition, skillName, cityName, scaleName, pageable);

        return announcements.map(teamMemberAnnouncement -> announcementInformMenuAssembler.mapToAnnouncementInformMenu(teamMemberAnnouncement, optionalMemberId));
    }

}
