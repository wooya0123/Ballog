package notfound.ballog.domain.team.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {

    private Integer teamMemberId;

    private String nickname;

}
