package notfound.ballog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // 허용할 url 배열
    private static final String[] commonUrl = new String[] {
            "/swagger-ui/**",       // 스웨거
            "/swagger-resources/**",// 스웨거
            "/v3/api-docs/**",      // 스웨거
            "/api-docs/**"          // 스웨거
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Restful API를 사용하므로, csrf는 사용할 필요가 없다
            .csrf(CsrfConfigurer::disable)
            // 토큰 방식을 사용하므로, 서버에서 session을 관리하지 않음. 따라서 STATELESS로 설정
            .sessionManagement(
                    sessionManagement -> sessionManagement
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 필터 적용 시킬 url과 아닌 url 구분(초기엔 다 허용)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                // 로그인 없이 허용할 url
                .requestMatchers(commonUrl).permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
