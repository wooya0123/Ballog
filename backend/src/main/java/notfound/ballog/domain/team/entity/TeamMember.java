package notfound.ballog.domain.team.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import notfound.ballog.domain.team.request.TeamMemberAddRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {

//    @Id
//    @SequenceGenerator(
//            name = "team_member_sequence",
//            sequenceName = "team_member_sequence"
//            // default value 50
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "team_member_sequence"
//    )

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamMemberId;

    private UUID userId;

    private Integer teamId;

    private String role;

    private LocalDateTime joinedAt;

    public static TeamMember of(UUID userId, TeamMemberAddRequest req) {
        return TeamMember.builder()
                .userId(userId)
                .teamId(req.getTeamId())
                .role(req.getRole())
                .joinedAt(LocalDateTime.now())
                .build();
    }

}
