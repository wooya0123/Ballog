package notfound.ballog.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.dto.AuthDto;
import notfound.ballog.domain.auth.entity.Auth;
import notfound.ballog.domain.auth.repository.AuthRepository;
import notfound.ballog.domain.auth.request.SignUpRequest;
import notfound.ballog.domain.user.dto.UserDto;
import notfound.ballog.domain.user.entity.User;
import notfound.ballog.domain.user.service.UserService;
import notfound.ballog.exception.DuplicateEmailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    // 회원가입
    public void addAuth(SignUpRequest request){
        // 이미 가입된 회원인지 체크
        Boolean userExist = authRepository.existsByEmail(request.getEmail());
        if(userExist){
            throw new DuplicateEmailException(BaseResponseStatus.DUPLICATE_EMAIL);
        }
        log.info("nickname from request: {}", request.getNickName());

        // 유저 생성
        UserDto userDto = UserDto.builder()
                .nickName(request.getNickName())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .profileImageUrl(request.getProfileImageUrl())
                .build();
        User savedUser = userService.addUser(userDto);

        // Auth 생성
        AuthDto authDto = AuthDto.builder()
                .user(savedUser)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();
        Auth newAuth = Auth.of(authDto);
        authRepository.save(newAuth);


    }
}
