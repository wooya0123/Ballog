package notfound.ballog.domain.team.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberAddRequest {

    private Integer teamId;

    // '운영진', '멤버' ,'용병'
    private String role;

}
