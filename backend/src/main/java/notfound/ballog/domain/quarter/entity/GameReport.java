package notfound.ballog.domain.quarter.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameReport {

    @Id
    @SequenceGenerator(
            name = "game_report_sequence",
            sequenceName = "game_report_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "game_report_sequence"
    )
    private Integer reportId;

    private UUID userId;

    private Integer quarterId;

    @Column(columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private Map<String, Object> jsonData;

    private LocalDateTime createdAt;

    public GameReport(UUID userId, Integer quarterId, Map<String, Object> jsonData) {
        this.userId = userId;
        this.quarterId = quarterId;
        this.jsonData = jsonData;
        this.createdAt = LocalDateTime.now();
    }

}
