package notfound.ballog.domain.video.repository;

import notfound.ballog.domain.video.entity.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HighlightRepository extends JpaRepository<Highlight, Integer> {
    List<Highlight> findAllByVideoId(Integer videoId);
}
