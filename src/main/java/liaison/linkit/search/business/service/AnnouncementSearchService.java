package liaison.linkit.search.business.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import liaison.linkit.search.presentation.dto.AnnouncementSearchResponseDTO;
import liaison.linkit.team.business.assembler.AnnouncementInformMenuAssembler;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public AnnouncementSearchResponseDTO searchAnnouncements(
        final Optional<Long> optionalMemberId,
        List<String> subPosition,
        List<String> cityName,
        List<String> scaleName,
        Pageable pageable) {
        // 쿼리 파라미터가 모두 비어있는 경우: 기본 검색
        boolean isDefaultSearch = (subPosition == null || subPosition.isEmpty())
            && (cityName == null || cityName.isEmpty())
            && (scaleName == null || scaleName.isEmpty());

        if (isDefaultSearch) {
            Pageable hotPageable = PageRequest.of(0, 6);
            List<TeamMemberAnnouncement> hotAnnouncements = teamMemberAnnouncementQueryAdapter
                .findHotAnnouncements(hotPageable).getContent();

            List<AnnouncementInformMenu> hotAnnouncementDTOs = hotAnnouncements.stream()
                .map(teamMemberAnnouncement -> announcementInformMenuAssembler
                    .mapToAnnouncementInformMenu(teamMemberAnnouncement, optionalMemberId))
                .toList();

            List<Long> excludeAnnouncementIds = hotAnnouncements.stream()
                .map(TeamMemberAnnouncement::getId)
                .toList();

            Page<TeamMemberAnnouncement> remainingAnnouncements = teamMemberAnnouncementQueryAdapter
                .findExcludedAnnouncements(excludeAnnouncementIds, pageable);
            Page<AnnouncementInformMenu> remainingAnnouncementDTOs = remainingAnnouncements
                .map(teamMemberAnnouncement -> announcementInformMenuAssembler
                    .mapToAnnouncementInformMenu(teamMemberAnnouncement, optionalMemberId));

            return AnnouncementSearchResponseDTO.builder()
                .hotAnnouncements(hotAnnouncementDTOs)
                .defaultAnnouncements(remainingAnnouncementDTOs)
                .build();
        } else {
            Page<TeamMemberAnnouncement> announcements = teamMemberAnnouncementQueryAdapter.findAll(subPosition,
                cityName, scaleName, pageable);
            Page<AnnouncementInformMenu> announcementDTOs = announcements
                .map(teamMemberAnnouncement -> announcementInformMenuAssembler
                    .mapToAnnouncementInformMenu(teamMemberAnnouncement, optionalMemberId));

            return AnnouncementSearchResponseDTO.builder()
                .hotAnnouncements(Collections.emptyList())
                .defaultAnnouncements(announcementDTOs)
                .build();
        }
    }

}
