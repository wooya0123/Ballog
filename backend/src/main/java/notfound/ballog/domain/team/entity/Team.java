package notfound.ballog.domain.team.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import notfound.ballog.domain.team.request.TeamAddRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    //    @Id
//    @SequenceGenerator(
//            name = "team_sequence",
//            sequenceName = "team_sequence",
//            allocationSize = 5
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "team_sequence"
//    )

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamId;

    private String teamName;

    private String foundationDate;

    private String logoImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Team of(TeamAddRequest req){
        LocalDateTime now = LocalDateTime.now();

        return Team.builder()
                .teamName(req.getTeamName())
                .foundationDate(req.getFoundationDate())
                .logoImageUrl(req.getLogoImage())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
