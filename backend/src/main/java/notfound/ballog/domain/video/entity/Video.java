package notfound.ballog.domain.video.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.match.entity.Match;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_seq_generator")
    @SequenceGenerator(
            name = "video_seq_generator",
            sequenceName = "video_seq",
            allocationSize = 1
    )
    private Integer videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    private Integer quarterNumber;
    private String videoUrl;

    @Column(columnDefinition = "INTERVAL")
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    private Duration duration;

    public static Video of(Match match, Integer quarterNumber, String videoUrl, Duration duration) {
        return Video.builder()
                .match(match)
                .quarterNumber(quarterNumber)
                .videoUrl(videoUrl)
                .duration(duration)
                .build();
    }

}
