package notfound.ballog.domain.auth.service;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.entity.Auth;
import notfound.ballog.domain.auth.repository.AuthRepository;
import notfound.ballog.exception.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    /** 회원가입 시 이메일 중복 확인 */
    public Boolean existsByEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    /** 로그인 시 검증 */
    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 auth 조회
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException(BaseResponseStatus.USER_NOT_FOUND));

        return new CustomUserDetails(auth);
    }
}
