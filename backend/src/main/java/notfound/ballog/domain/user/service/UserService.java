package notfound.ballog.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import notfound.ballog.domain.user.dto.UserDto;
import notfound.ballog.domain.user.entity.PlayerCard;
import notfound.ballog.domain.user.entity.User;
import notfound.ballog.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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
}

