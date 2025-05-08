package notfound.ballog.domain.video.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Highlight {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_seq_generator")
    @SequenceGenerator(
            name = "video_seq_generator",
            sequenceName = "video_seq",
            allocationSize = 1
    )
    private Integer highlightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(nullable = false)
    private String highlightName;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
