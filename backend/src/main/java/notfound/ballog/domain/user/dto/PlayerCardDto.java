package notfound.ballog.domain.user.dto;

import lombok.*;
import notfound.ballog.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerCardDto {
    private User user;
    private String playStyle;
    private String rank;
    private int speed;
    private int stamina;
    private int attack;
    private int defense;
    private int recovery;
}
