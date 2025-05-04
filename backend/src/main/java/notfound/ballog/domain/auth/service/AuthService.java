package notfound.ballog.domain.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.jwt.JwtTokenProvider;
import notfound.ballog.common.jwt.TokenBlacklistService;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.dto.AuthDto;
import notfound.ballog.domain.auth.dto.JwtTokenDto;
import notfound.ballog.domain.auth.entity.Auth;
import notfound.ballog.domain.auth.repository.AuthRepository;
import notfound.ballog.domain.auth.request.LoginRequest;
import notfound.ballog.domain.auth.request.SignUpRequest;
import notfound.ballog.domain.auth.response.LoginResponse;
import notfound.ballog.domain.user.dto.UserDto;
import notfound.ballog.domain.user.dto.UserIdDto;
import notfound.ballog.domain.user.entity.User;
import notfound.ballog.domain.user.service.UserService;
import notfound.ballog.exception.DuplicateDataException;
import notfound.ballog.exception.ValidationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public void signUp(SignUpRequest request){
        // 1. 이메일 중복 체크
        if (customUserDetailsService.existsByEmail(request.getEmail())) {
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
        auth.setRefreshToken(token.getRefreshToken());
        Auth savedAuth = authRepository.save(auth);

        UserIdDto userIdDto = UserIdDto.fromAuth(savedAuth);
        return LoginResponse.fromAuth(token, userIdDto);
    }

    @Transactional
    public void logOut(Integer authId, String accessToken) {
        // 1. access토큰 블랙리스트 처리
        tokenBlacklistService.addToBlacklist(accessToken);

        // 2. DB에 저장된 refresh 토큰 삭제
        Auth auth = authRepository.findById(authId)
                        .orElseThrow(() -> new ValidationException(BaseResponseStatus.USER_NOT_FOUND));
        auth.setRefreshToken(null);
        authRepository.save(auth);
    }
}
