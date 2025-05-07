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

    @Column(nullable = false)
    private String stadiumName;

    @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point topLeft;

    @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point topRight;

    @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point bottomLeft;

    @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point bottomRight;

}
