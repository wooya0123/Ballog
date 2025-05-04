package notfound.ballog.domain.auth.service;

import jakarta.transaction.Transactional;
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
import notfound.ballog.exception.DuplicateDataException;
import notfound.ballog.exception.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomUserDetailService customUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final UserService userService;

    /** 회원가입 */
    @Transactional
    public void signUp(SignUpRequest request){
        // 1. 이메일 중복 체크
        if (customUserDetailService.existsByEmail(request.getEmail())) {
            throw new ValidationException(BaseResponseStatus.DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화
        String password = passwordEncoder.encode(request.getPassword());

        // 3. 유저 생성
        User newUser = request.toUserEntity(request);
        User savedUser = userService.signUp(newUser);

        // 3. Auth 생성
        Auth newAuth = request.toAuthEntity(savedUser, request.getEmail(), password);
        authRepository.save(newAuth);
    }
}
