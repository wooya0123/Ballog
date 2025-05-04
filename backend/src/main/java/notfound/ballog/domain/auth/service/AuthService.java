package notfound.ballog.domain.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.jwt.JwtTokenProvider;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.dto.JwtTokenDto;
import notfound.ballog.domain.auth.entity.Auth;
import notfound.ballog.domain.auth.repository.AuthRepository;
import notfound.ballog.domain.auth.request.LoginRequest;
import notfound.ballog.domain.auth.request.SignUpRequest;
import notfound.ballog.domain.auth.response.CheckEmailResponse;
import notfound.ballog.domain.auth.response.LoginResponse;
import notfound.ballog.domain.auth.response.TokenRefreshResponse;
import notfound.ballog.domain.user.dto.UserIdDto;
import notfound.ballog.domain.user.entity.User;
import notfound.ballog.domain.user.service.UserService;
import notfound.ballog.exception.DuplicateDataException;
import notfound.ballog.exception.ValidationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignUpRequest request){
        String email = request.getEmail();
        String password = passwordEncoder.encode(request.getPassword());

        Optional<Auth> existingAuth = authRepository.findByEmail(email);
        if (existingAuth.isPresent()) {
            User user  = existingAuth.get().getUser();
            Auth auth = request.toAuthEntity(user, email, password);
            // 탈퇴한 사용자라면 복구
            if (!auth.getIsActive()) {
                auth.changeIsActive(true);
                authRepository.save(auth);
                return;
            } else {
                throw new DuplicateDataException(BaseResponseStatus.DUPLICATE_EMAIL);
            }
        }

        // 1. 신규 유저 생성
        User newUser = request.toUserEntity(request);
        User savedUser = userService.signUp(newUser);

        // 2. 신규 Auth 생성
        Auth newAuth = request.toAuthEntity(savedUser, email, password);
        authRepository.save(newAuth);
    }

    @Transactional
    public LoginResponse login(LoginRequest request){
        // 1. 이메일로 auth 조회
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        Auth auth = userDetails.getAuth();

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new ValidationException(BaseResponseStatus.PASSWORD_MISMATCH);
        }

        // 3. JWT 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
        JwtTokenDto token = jwtTokenProvider.generateToken(authentication);

        // 4. refreshToken 저장
        auth.changeRefreshToken(token.getRefreshToken());
        Auth savedAuth = authRepository.save(auth);

        UserIdDto userIdDto = UserIdDto.of(savedAuth);
        return LoginResponse.of(token, userIdDto);
    }

    @Transactional
    public void logOut(Integer authId) {
        // DB에 저장된 refresh 토큰 삭제
        Auth auth = authRepository.findById(authId)
                        .orElseThrow(() -> new ValidationException(BaseResponseStatus.USER_NOT_FOUND));
        auth.changeRefreshToken(null);
        authRepository.save(auth);
    }

    @Transactional
    public TokenRefreshResponse refreshToken(Integer authId, String refreshToken) {
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new ValidationException(BaseResponseStatus.USER_NOT_FOUND));

        // 1. 토큰 만료 체크
        jwtTokenProvider.validateToken(refreshToken);

        // 2. db 토큰과 비교
        if (!auth.getRefreshToken().equals(refreshToken)) {
            throw new ValidationException(BaseResponseStatus.INVALID_TOKEN);
        }

        // 3. 탈퇴한 사용자인지 확인
        if (!auth.getIsActive()) {
            throw new ValidationException(BaseResponseStatus.USER_INACTIVE);
        }

        // 4. 토큰 재발급
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        JwtTokenDto token = jwtTokenProvider.generateToken(authentication);
        auth.changeRefreshToken(token.getRefreshToken());
        authRepository.save(auth);

        return TokenRefreshResponse.of(token);
    }

    public CheckEmailResponse checkEmail(String email) {
        boolean isExist = authRepository.existsByEmailAndIsActiveTrue(email);
        return CheckEmailResponse.of(!isExist);
    }
}
