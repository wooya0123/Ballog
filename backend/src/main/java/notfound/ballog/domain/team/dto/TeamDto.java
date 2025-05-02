package notfound.ballog.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.team.entity.Team;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {

    private String teamName;

    private String logoImageUrl;

    private String foundationDate;

    public static TeamDto of(Team team) {
        return TeamDto.builder()
                .teamName(team.getTeamName())
                .logoImageUrl(team.getLogoImageUrl())
                .foundationDate(team.getFoundationDate())
                .build();
    }

}
