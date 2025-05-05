package notfound.ballog.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import notfound.ballog.domain.user.entity.PlayerCard;

@Getter
@Builder
public class PlayerCardStatListDto {
    private Integer speed;
    private Integer stamina;
    private Integer attack;
    private Integer defense;
    private Integer recovery;

    public static PlayerCardStatListDto of(PlayerCard playerCard) {
        return PlayerCardStatListDto.builder()
                .speed(playerCard.getSpeed())
                .stamina(playerCard.getStamina())
                .attack(playerCard.getAttack())
                .defense(playerCard.getDefense())
                .build();
    }
}
