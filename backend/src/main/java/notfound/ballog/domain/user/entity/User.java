package notfound.ballog.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import notfound.ballog.domain.user.dto.UserDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nickName;

    @Column(nullable = false, length = 30)
    private String gender;

    private LocalDate birthDate;
    private String profileImageUrl;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .nickName(userDto.getNickName())
                .gender(userDto.getGender())
                .birthDate(userDto.getBirthDate())
                .profileImageUrl(userDto.getProfileImageUrl())
                .build();
    }
}
