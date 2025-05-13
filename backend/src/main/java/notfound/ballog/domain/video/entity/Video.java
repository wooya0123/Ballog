package notfound.ballog.domain.video.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.match.entity.Match;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE video SET is_deleted = TRUE WHERE video_id = ?")
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

    @Column(columnDefinition = "TEXT")
    private String videoUrl;

    @Column(columnDefinition = "INTERVAL")
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    private Duration duration;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(columnDefinition = "boolean default false")
    private boolean uploadSuccess;

    @Column(columnDefinition = "boolean default false")
    private boolean deleted;

    public static Video of(Match match, Integer quarterNumber, String videoUrl, Duration duration) {
        return Video.builder()
                .match(match)
                .quarterNumber(quarterNumber)
                .videoUrl(videoUrl)
                .duration(duration)
                .build();
    }

    public void updateUploadSuccess() {
        this.uploadSuccess = true;
    }

    public void delete() {
        this.deleted = true;
    }
}
