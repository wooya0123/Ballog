package notfound.ballog.domain.match.repository;

import notfound.ballog.domain.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer>, MatchRepositoryCustom {

}
