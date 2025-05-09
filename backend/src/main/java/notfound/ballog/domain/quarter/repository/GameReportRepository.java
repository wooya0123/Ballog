package notfound.ballog.domain.quarter.repository;

import notfound.ballog.domain.quarter.entity.GameReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameReportRepository extends JpaRepository<GameReport, Integer> {
}
