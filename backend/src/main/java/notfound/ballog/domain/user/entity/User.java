package notfound.ballog.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column
    private String gender;

    private LocalDate birthDate;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    public void updateUser(UpdateUserRequest request) {
        this.nickname = request.getNickname();
        this.gender = request.getGender();
        this.birthDate = request.getBirthDate();
    }

    public void updateProfileImage(UpdateProfileImageRequest request) {
        this.profileImageUrl = request.getProfileImageUrl();
    }

    public void reactivate(String nickname, LocalDate birthDate, String profileImageUrl) {
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.profileImageUrl = profileImageUrl;
    }
}
