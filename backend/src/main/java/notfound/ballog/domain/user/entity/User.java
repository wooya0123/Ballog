package notfound.ballog.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import notfound.ballog.domain.user.dto.UserDto;
import notfound.ballog.domain.user.request.UpdateProfileImageRequest;
import notfound.ballog.domain.user.request.UpdateUserRequest;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String nickname;

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

    public User updateUser(UpdateUserRequest request) {
        this.nickname = request.getNickname();
        this.gender = request.getGender();
        this.birthDate = request.getBirthDate();
        return this;
    }

    public User updateProfileImage(UpdateProfileImageRequest request) {
        this.profileImageUrl = request.getProfileImageUrl();
        return this;
    }
}
