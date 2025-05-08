package notfound.ballog.domain.quarter.repository;

import notfound.ballog.domain.quarter.entity.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuarterRepository extends JpaRepository<Quarter, Integer> {
    List<Quarter> findAllByMatchId(Integer matchId);
}
