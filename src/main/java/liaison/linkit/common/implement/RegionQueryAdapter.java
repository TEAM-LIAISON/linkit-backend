package liaison.linkit.common.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileRegion;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.profile.domain.repository.region.ProfileRegionRepository;
import liaison.linkit.profile.exception.region.ProfileRegionNotFoundException;
import liaison.linkit.team.domain.TeamRegion;
import liaison.linkit.team.domain.announcement.AnnouncementRegion;
import liaison.linkit.team.domain.repository.announcement.AnnouncementRegionRepository;
import liaison.linkit.team.domain.repository.region.RegionRepository;
import liaison.linkit.team.domain.repository.region.TeamRegionRepository;
import liaison.linkit.team.exception.announcement.AnnouncementRegionNotFoundException;
import liaison.linkit.team.exception.region.TeamRegionNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class RegionQueryAdapter {

    private final RegionRepository regionRepository;
    private final ProfileRegionRepository profileRegionRepository;
    private final AnnouncementRegionRepository announcementRegionRepository;
    private final TeamRegionRepository teamRegionRepository;

    public Region findByCityNameAndDivisionName(final String cityName, final String divisionName) {
        return regionRepository.findByCityNameAndDivisionName(cityName, divisionName);
    }

    public boolean existsProfileRegionByProfileId(final Long profileId) {
        return profileRegionRepository.existsProfileRegionByProfileId(profileId);
    }

    public ProfileRegion findProfileRegionByProfileId(final Long profileId) {
        return profileRegionRepository.findProfileRegionByProfileId(profileId)
                .orElseThrow(() -> ProfileRegionNotFoundException.EXCEPTION);
    }

    public boolean existsAnnouncementRegionByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        return announcementRegionRepository.existsAnnouncementRegionByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }

    public AnnouncementRegion findAnnouncementRegionByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        return announcementRegionRepository.findAnnouncementRegionByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> AnnouncementRegionNotFoundException.EXCEPTION);
    }

    public boolean existsTeamRegionByTeamId(final Long teamId) {
        return teamRegionRepository.existsTeamRegionByTeamId(teamId);
    }

    public TeamRegion findTeamRegionByTeamId(final Long teamId) {
        return teamRegionRepository.findTeamRegionByTeamId(teamId)
                .orElseThrow(() -> TeamRegionNotFoundException.EXCEPTION);
    }
}
