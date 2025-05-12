package notfound.ballog.domain.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.user.dto.PlayerCardStatListDto;
import notfound.ballog.domain.user.entity.PlayerCard;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPlayerCardResponse {
    private String nickname;
    private PlayerCardStatListDto cardStats;

    public static GetPlayerCardResponse of(PlayerCard playerCard, PlayerCardStatListDto cardStatList) {
        return GetPlayerCardResponse.builder()
                .nickname(playerCard.getUser().getNickname())
                .cardStats(cardStatList)
                .build();
    }
}
