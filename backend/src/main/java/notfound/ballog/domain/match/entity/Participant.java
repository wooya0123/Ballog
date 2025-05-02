package notfound.ballog.domain.match.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    //    @Id
//    @SequenceGenerator(
//            name = "participant_sequence",
//            sequenceName = "participant_sequence"
//            // default value 50
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "participant_sequence"
//    )

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer participantId;

    private Integer matchId;

    private Integer userId;

    private String participantType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
