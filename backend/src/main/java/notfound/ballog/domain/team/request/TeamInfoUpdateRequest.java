package notfound.ballog.domain.team.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamInfoUpdateRequest {

    private Integer teamId;

    private String teamName;

    private LocalDate foundationDate;

    private String logoImage;

}
