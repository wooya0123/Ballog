package notfound.ballog.domain.match.repository;

import notfound.ballog.domain.match.dto.MatchDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MatchRepositoryCustom {

    List<MatchDto> findMatchesByUserIdAndMonth(UUID userId, LocalDate startOfMonth, LocalDate endOfMonth);

    List<MatchDto> findMatchesByTeamIdAndMonth(Integer teamId, LocalDate startOfMonth, LocalDate endOfMonth);

    Integer findMatchIdByUserIdAndMatchDate(UUID userId, LocalDate matchDate);

}
