package notfound.ballog.domain.team.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamAddRequest {

    private String teamName;

    private String foundationDate;

    private String logoImage;

}
