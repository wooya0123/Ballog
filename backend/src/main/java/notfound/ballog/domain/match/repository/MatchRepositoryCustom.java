package notfound.ballog.domain.match.repository;

import notfound.ballog.domain.match.dto.MatchDto;
import notfound.ballog.domain.quarter.response.AddQuarterAndGameReportResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MatchRepositoryCustom {

    List<MatchDto> findMatchesByUserIdAndMonth(UUID userId, LocalDate startOfMonth, LocalDate endOfMonth);

    List<MatchDto> findMatchesByTeamIdAndMonth(Integer teamId, LocalDate startOfMonth, LocalDate endOfMonth);

    AddQuarterAndGameReportResponse findMatchIdByUserIdAndMatchDate(UUID userId, LocalDate matchDate);

    List<MatchDto> findMatchesByUserIdAndMatchDates(UUID userId, List<LocalDate> matchDates);

}
