package notfound.ballog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
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
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                    // 로그인 없이 허용할 url
                    .requestMatchers(commonUrl).permitAll()
                    // 초기 단계에서는 모든 요청 허용
                    .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
