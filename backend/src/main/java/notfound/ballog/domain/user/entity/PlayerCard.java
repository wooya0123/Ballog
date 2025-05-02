package notfound.ballog.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import notfound.ballog.domain.user.dto.PlayerCardDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerCard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_card_seq_generator")
    @SequenceGenerator(
            name = "player_card_seq_generator",
            sequenceName = "player_card_seq",
            allocationSize = 1
    )
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 50)
    private String playStyle;

    @Column(length = 50)
    private String rank;

    @Column(columnDefinition = "SMALLINT")
    private int speed;

    @Column(columnDefinition = "SMALLINT")
    private int stamina;

    @Column(columnDefinition = "SMALLINT")
    private int attack;

    @Column(columnDefinition = "SMALLINT")
    private int defense;

    @Column(columnDefinition = "SMALLINT")
    private int recovery;

    public static PlayerCard of(User user) {
        return PlayerCard.builder()
                .user(user)
                .playStyle(null)
                .rank(null)
                .speed(0)
                .stamina(0)
                .attack(0)
                .defense(0)
                .recovery(0)
                .build();
    }

}
