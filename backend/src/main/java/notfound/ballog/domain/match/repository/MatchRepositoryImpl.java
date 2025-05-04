package notfound.ballog.domain.match.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import notfound.ballog.domain.match.dto.MatchDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

import static notfound.ballog.domain.match.entity.QMatch.match;
import static notfound.ballog.domain.match.entity.QParticipant.participant;
import static notfound.ballog.domain.match.entity.QStadium.stadium;

@Repository
@RequiredArgsConstructor
public class MatchRepositoryImpl implements MatchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MatchDto> findMatchesByUserIdAndMonth(UUID userId, String month) {
        // "2025-04" 형식에서 년도와 월을 추출
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthValue = Integer.parseInt(parts[1]);
        
        // 해당 월의 시작일과 마지막일 계산
        LocalDate startOfMonth = LocalDate.of(year, monthValue, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        
        return queryFactory
                .select(Projections.constructor(MatchDto.class,
                    match.matchId,
                    stadium.stadiumName,
                    match.matchDate,
                    match.startTime,
                    match.endTime))
                .from(match)
                .join(participant).on(match.matchId.eq(participant.matchId).and(participant.userId.eq(userId)))
                .join(stadium).on(match.stadiumId.eq(stadium.stadiumId))
                .where(match.matchDate.between(startOfMonth, endOfMonth))
                .orderBy(match.matchDate.asc())
                .fetch();
    }

    @Override
    public List<MatchDto> findMatchesByTeamIdAndMonth(Integer teamId, String month) {
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthValue = Integer.parseInt(parts[1]);

        LocalDate startOfMonth = LocalDate.of(year, monthValue, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        return queryFactory
                .select(Projections.constructor(MatchDto.class,
                        match.matchId,
                        stadium.stadiumName,
                        match.matchDate,
                        match.startTime,
                        match.endTime))
                .from(match)
                .join(participant).on(match.matchId.eq(participant.matchId))
                .join(stadium).on(match.stadiumId.eq(stadium.stadiumId))
                .where(match.teamId.eq(teamId), match.matchDate.between(startOfMonth, endOfMonth))
                .orderBy(match.matchDate.asc())
                .fetch();
    }

}
