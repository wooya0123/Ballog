package notfound.ballog.domain.team.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCard {

    //    @Id
//    @SequenceGenerator(
//            name = "team_card_sequence",
//            sequenceName = "team_card_sequence"
//            // default value 50
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "team_card_sequence"
//    )

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamCardId;

    private Integer teamId;

    private int avgSpeed;

    private int avgStamina;

    private int avgAttack;

    private int avgDefense;

    private int avgRecovery;

    public static TeamCard of(Integer teamId){
        return TeamCard.builder()
                .teamId(teamId)
                .build();
    }

}
