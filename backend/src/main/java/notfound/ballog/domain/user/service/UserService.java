package notfound.ballog.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.entity.Auth;
import notfound.ballog.domain.auth.repository.AuthRepository;
import notfound.ballog.domain.user.entity.User;
import notfound.ballog.domain.user.repository.UserRepository;
import notfound.ballog.domain.user.request.UpdateProfileImageRequest;
import notfound.ballog.domain.user.request.UpdateUserRequest;
import notfound.ballog.domain.user.response.GetUserResponse;
import notfound.ballog.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final PlayerCardService playerCardService;

    // 회원가입
    @Transactional
    public User signUp(User newUser) {
        // 유저 저장
        User savedUser = userRepository.save(newUser);

        // 플레이어 카드 생성
        playerCardService.addPlayerCard(savedUser);

        return savedUser;
    }

    public GetUserResponse getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
        Auth auth = authRepository.findByUser_UserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
        String email = auth.getEmail();
        return GetUserResponse.of(user, email);
    }

    public void updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
        user.updateUser(request);
        userRepository.save(user);
    }

    public void updateProfileImage(UUID userId, UpdateProfileImageRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
        user.updateProfileImage(request);
        userRepository.save(user);
    }

    public User reactivateUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser;
    }
}

