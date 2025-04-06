package liaison.linkit.profile.domain.repository.profile;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.common.domain.QProfileState;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.profile.domain.position.QProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.domain.state.QProfileCurrentState;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
@Slf4j
public class ProfileCustomRepositoryImpl implements ProfileCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Profile> findByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Profile result =
                jpaQueryFactory
                        .selectFrom(profile)
                        .where(profile.member.id.eq(memberId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Profile> findByEmailId(final String emailId) {
        QProfile profile = QProfile.profile;

        Profile result =
                jpaQueryFactory
                        .selectFrom(profile)
                        .where(profile.member.emailId.eq(emailId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(profile)
                        .where(profile.member.id.eq(memberId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        jpaQueryFactory
                .update(profile)
                .set(profile.status, StatusType.DELETED)
                .where(profile.member.id.eq(memberId))
                .execute();
    }

    @Override
    public List<Profile> findHomeTopProfiles(final int limit) {
        QProfile qProfile = QProfile.profile;

        return jpaQueryFactory
                .selectFrom(qProfile)
                .leftJoin(qProfile.member, QMember.member)
                .fetchJoin()
                .where(qProfile.status.eq(StatusType.USABLE).and(qProfile.isProfilePublic.eq(true)))
                .orderBy(
                        new CaseBuilder()
                                .when(qProfile.id.eq(42L))
                                .then(0)
                                .when(qProfile.id.eq(58L))
                                .then(1)
                                .when(qProfile.id.eq(57L))
                                .then(2)
                                .when(qProfile.id.eq(55L))
                                .then(3)
                                .when(qProfile.id.eq(73L))
                                .then(4)
                                .when(qProfile.id.eq(63L))
                                .then(5)
                                .otherwise(6)
                                .asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Page<Profile> findTopCompletionProfiles(final Pageable pageable) {
        QProfile qProfile = QProfile.profile;

        List<Profile> content =
                jpaQueryFactory
                        .selectFrom(qProfile)
                        .where(
                                qProfile.status
                                        .eq(StatusType.USABLE)
                                        .and(qProfile.isProfilePublic.eq(true)))
                        .orderBy(
                                new CaseBuilder()
                                        .when(qProfile.id.eq(42L))
                                        .then(0)
                                        .when(qProfile.id.eq(58L))
                                        .then(1)
                                        .when(qProfile.id.eq(57L))
                                        .then(2)
                                        .when(qProfile.id.eq(55L))
                                        .then(3)
                                        .when(qProfile.id.eq(73L))
                                        .then(4)
                                        .when(qProfile.id.eq(63L))
                                        .then(5)
                                        .when(qProfile.id.eq(33L))
                                        .then(6)
                                        .when(qProfile.id.eq(26L))
                                        .then(7)
                                        .otherwise(8)
                                        .asc())
                        .limit(6)
                        .fetch();

        // Pageable 정보와 함께 Page 객체로 반환 (항상 최대 6개의 레코드)
        return PageableExecutionUtils.getPage(content, pageable, content::size);
    }

    public CursorResponse<Profile> findAllExcludingIdsWithCursor(
            final List<Long> excludeProfileIds, final CursorRequest cursorRequest) {
        QProfile qProfile = QProfile.profile;
        QMember qMember = QMember.member;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;

        try {
            // 기본 조건
            BooleanExpression baseCondition =
                    qProfile.status.eq(USABLE).and(qProfile.isProfilePublic.eq(true));

            // 제외 ID 조건
            if (excludeProfileIds != null && !excludeProfileIds.isEmpty()) {
                baseCondition = baseCondition.and(qProfile.id.notIn(excludeProfileIds));
            }

            // 커서 조건 (이메일 ID 직접 사용)
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                baseCondition =
                        baseCondition.and(qProfile.member.emailId.ne(cursorRequest.getCursor()));
            }

            // 페이지 크기
            int size = (cursorRequest != null) ? cursorRequest.getSize() : 10;

            // 단일 쿼리로 모든 정보 조회 (Fetch Join)
            List<Profile> profiles =
                    jpaQueryFactory
                            .selectFrom(qProfile)
                            .distinct()
                            .join(qProfile.member, qMember)
                            .fetchJoin()
                            .leftJoin(qProfilePosition)
                            .on(qProfilePosition.profile.eq(qProfile))
                            .fetchJoin()
                            .leftJoin(qPosition)
                            .on(qProfilePosition.position.eq(qPosition))
                            .fetchJoin()
                            .leftJoin(qProfileRegion)
                            .on(qProfileRegion.profile.eq(qProfile))
                            .fetchJoin()
                            .leftJoin(qRegion)
                            .on(qProfileRegion.region.eq(qRegion))
                            .fetchJoin()
                            .where(baseCondition)
                            .orderBy(qProfile.createdAt.desc())
                            .limit(size + 1)
                            .fetch();

            // 빈 결과 체크
            if (profiles.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 페이지 체크
            boolean hasNext = profiles.size() > size;
            String nextCursor = null;

            // 결과 목록
            List<Profile> resultProfiles = hasNext ? profiles.subList(0, size) : profiles;

            // 다음 커서 설정
            if (hasNext) {
                Profile lastProfile = profiles.get(size);
                nextCursor = lastProfile.getMember().getEmailId();
            }

            return CursorResponse.of(resultProfiles, nextCursor);
        } catch (Exception e) {
            log.error("쿼리 오류: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    /** 필터링 조건으로 프로필을 커서 기반으로 조회합니다. */
    public CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest) {
        QProfile qProfile = QProfile.profile;

        try {
            long startTime = System.currentTimeMillis();
            log.info("findAllByFilteringWithCursor 쿼리 시작");

            // emailId로 profileId를 찾는 로직
            Long cursorProfileId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                try {
                    String cursorEmailId = cursorRequest.getCursor();
                    long cursorQueryStart = System.currentTimeMillis();
                    cursorProfileId =
                            jpaQueryFactory
                                    .select(qProfile.id)
                                    .from(qProfile)
                                    .where(qProfile.member.emailId.eq(cursorEmailId))
                                    .fetchOne();
                    log.info("커서 ID 조회 시간: {} ms", System.currentTimeMillis() - cursorQueryStart);
                } catch (Exception e) {
                    log.error("커서 처리 중 오류: {}", e.getMessage());
                    // 커서 처리 실패 시 계속 진행
                }
            }

            // 1. 필터링된 프로필 ID와 emailId를 함께 조회
            long idQueryStart = System.currentTimeMillis();
            JPAQuery<Tuple> profileQuery =
                    jpaQueryFactory
                            .select(qProfile.id, qProfile.member.emailId)
                            .distinct()
                            .from(qProfile)
                            .where(
                                    qProfile.status
                                            .eq(USABLE)
                                            .and(qProfile.isProfilePublic.eq(true)));

            // 커서 조건 추가 - 프로필 ID 기준
            if (cursorProfileId != null) {
                profileQuery = profileQuery.where(qProfile.id.lt(cursorProfileId));
            }

            // 포지션 필터링 (안전하게 수행)
            if (isNotEmpty(subPosition)) {
                try {
                    QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
                    QPosition qPosition = QPosition.position;

                    profileQuery
                            .leftJoin(qProfilePosition)
                            .on(qProfilePosition.profile.eq(qProfile))
                            .leftJoin(qPosition)
                            .on(qProfilePosition.position.eq(qPosition))
                            .where(qPosition.subPosition.in(subPosition));
                } catch (Exception e) {
                    log.error("포지션 필터링 중 오류: {}", e.getMessage());
                }
            }

            // 지역 필터링 (안전하게 수행)
            if (isNotEmpty(cityName)) {
                try {
                    QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
                    QRegion qRegion = QRegion.region;

                    profileQuery
                            .leftJoin(qProfileRegion)
                            .on(qProfileRegion.profile.eq(qProfile))
                            .leftJoin(qRegion)
                            .on(qProfileRegion.region.eq(qRegion))
                            .where(qRegion.cityName.in(cityName));
                } catch (Exception e) {
                    log.error("지역 필터링 중 오류: {}", e.getMessage());
                }
            }

            // 상태 필터링 (안전하게 수행)
            if (isNotEmpty(profileStateName)) {
                try {
                    QProfileCurrentState qProfileCurrentState =
                            QProfileCurrentState.profileCurrentState;
                    QProfileState qProfileState = QProfileState.profileState;

                    profileQuery
                            .leftJoin(qProfileCurrentState)
                            .on(qProfileCurrentState.profile.eq(qProfile))
                            .leftJoin(qProfileState)
                            .on(qProfileCurrentState.profileState.eq(qProfileState))
                            .where(qProfileState.profileStateName.in(profileStateName));
                } catch (Exception e) {
                    log.error("상태 필터링 중 오류: {}", e.getMessage());
                }
            }

            // ID 기준으로 정렬 및 제한 (안전하게 계산)
            int requestedSize = 10; // 기본값
            if (cursorRequest != null) {
                requestedSize = Math.max(1, cursorRequest.getSize());
            }
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            List<Tuple> profileTuples;
            try {
                profileTuples =
                        profileQuery
                                .orderBy(qProfile.id.desc()) // ID 기준 정렬
                                .limit(pageSize + 1) // 다음 페이지 확인을 위해 +1
                                .fetch();
                log.info("필터링된 ID 목록 조회 시간: {} ms", System.currentTimeMillis() - idQueryStart);
            } catch (Exception e) {
                log.error("필터링된 ID 목록 조회 중 오류: {}", e.getMessage());
                return CursorResponse.of(List.of(), null);
            }

            // 조회할 프로필이 없는 경우 빈 응답 반환
            if (profileTuples == null || profileTuples.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // profileIds 추출 (안전하게 처리)
            List<Long> profileIds = new ArrayList<>();
            try {
                for (int i = 0;
                        i < (profileTuples.size() > pageSize ? pageSize : profileTuples.size());
                        i++) {
                    if (profileTuples.get(i) != null
                            && profileTuples.get(i).get(qProfile.id) != null) {
                        profileIds.add(profileTuples.get(i).get(qProfile.id));
                    }
                }
            } catch (Exception e) {
                log.error("필터링된 ID 추출 중 오류: {}", e.getMessage());
                // 오류 발생 시 수집된 profileIds로 계속 진행
            }

            // ID 목록이 비어 있으면 빈 결과 반환
            if (profileIds.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 커서 계산
            boolean hasNext = profileTuples.size() > pageSize;
            String nextCursor = null;

            // 다음 페이지가 있는 경우 (안전하게 처리)
            if (hasNext && profileTuples.size() > pageSize) {
                try {
                    Tuple lastTuple = profileTuples.get(pageSize);
                    if (lastTuple != null && lastTuple.get(qProfile.member.emailId) != null) {
                        nextCursor = lastTuple.get(qProfile.member.emailId);
                    }
                } catch (Exception e) {
                    log.error("다음 커서 계산 중 오류: {}", e.getMessage());
                }
            }

            // 2. 실제 데이터 조회 - 생성 시간 기준으로 정렬
            long detailQueryStart = System.currentTimeMillis();
            List<Profile> content;

            try {
                content =
                        jpaQueryFactory
                                .selectFrom(qProfile)
                                .where(qProfile.id.in(profileIds))
                                .orderBy(qProfile.createdAt.desc()) // 생성 시간 기준 내림차순 정렬
                                .fetch();
                log.info("필터링된 상세 프로필 조회 시간: {} ms", System.currentTimeMillis() - detailQueryStart);
            } catch (Exception e) {
                log.error("필터링된 상세 프로필 조회 중 오류: {}", e.getMessage());
                return CursorResponse.of(List.of(), null);
            }

            log.info("전체 필터링 쿼리 처리 시간: {} ms", System.currentTimeMillis() - startTime);

            return CursorResponse.of(content != null ? content : List.of(), nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllByFilteringWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    public List<Profile> findByMarketingConsentAndMajorPosition(final String majorPosition) {
        QProfile qProfile = QProfile.profile;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;

        return jpaQueryFactory
                .selectFrom(qProfile)
                .leftJoin(qProfilePosition)
                .on(qProfilePosition.profile.eq(qProfile))
                .leftJoin(qPosition)
                .on(qProfilePosition.position.eq(qPosition))
                .where(
                        qProfile.status.eq(StatusType.USABLE),
                        qProfile.isProfilePublic.eq(true),
                        qPosition.majorPosition.eq(majorPosition),
                        qProfile.member.memberBasicInform.marketingAgree.isTrue())
                .fetch();
    }
}
