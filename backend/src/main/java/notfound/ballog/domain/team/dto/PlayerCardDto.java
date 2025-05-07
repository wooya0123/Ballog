package notfound.ballog.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCardDto {

    private String nickName;

    private String playStyle;

    private String rank;

    private String cardImageUrl;

    private CardStat cardStats;

}
