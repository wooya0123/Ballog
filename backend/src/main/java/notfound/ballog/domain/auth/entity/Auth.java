package notfound.ballog.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import notfound.ballog.domain.auth.dto.AuthDto;
import notfound.ballog.domain.user.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_seq_generator")
    @SequenceGenerator(
            name = "auth_seq_generator",
            sequenceName = "auth_seq",
            allocationSize = 1
    )
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public static Auth of(AuthDto authDto) {
        return Auth.builder()
                .user(authDto.getUser())
                .email(authDto.getEmail())
                .password(authDto.getPassword())
                .isActive(authDto.getIsActive())
                .build();
    }
}
