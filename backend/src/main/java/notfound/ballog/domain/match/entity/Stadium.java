package notfound.ballog.domain.match.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stadium {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer stadiumId;

    private String stadiumName;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point topLeft;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point topRight;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point bottomLeft;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point bottomRight;

}
