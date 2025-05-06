package notfound.ballog.domain.match.repository;

import notfound.ballog.domain.match.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Integer> {

    @Query("select s.stadiumName from Stadium s")
    List<String> findAllStadiumNames();

}
