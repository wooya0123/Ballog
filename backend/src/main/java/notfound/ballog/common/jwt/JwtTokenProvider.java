package notfound.ballog.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.dto.JwtTokenDto;
import notfound.ballog.domain.auth.entity.Auth;
import notfound.ballog.domain.auth.repository.AuthRepository;
import notfound.ballog.domain.auth.service.CustomUserDetails;
import notfound.ballog.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final AuthRepository authRepository;
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expire-ms}")
    private Long accessExpireMs;

    @Value("${jwt.refresh-expire-ms}")
    private Long refreshExpireMs;

    private Key signingKey;

    public JwtTokenProvider(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    /** 키 생성 */
    @PostConstruct
    public void init() {
        // Base64로 인코딩된 secret 문자열을 디코딩 → HMAC-SHA256 키 객체 생성
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /** JWT 토큰 생성 */
    public JwtTokenDto generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer authId = userDetails.getAuth().getId();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(authId))
                .claim("auth", authorities)
                .setExpiration(new Date((now + accessExpireMs)))
                .signWith(signingKey, SignatureAlgorithm.HS256) // HS256 알고리즘 서명
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(authId))
                .setExpiration(new Date((now + refreshExpireMs)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /** 토큰 파싱 및 claim 반환 */
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** 토큰 유효성 검사 */
    public Claims validateToken(String token) {
        try {
            return parseToken(token);
        } catch (ExpiredJwtException e) {
            throw new ValidationException(BaseResponseStatus.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new ValidationException(BaseResponseStatus.INVALID_TOKEN);
        }
    }

    /** 토큰에서 Authentication 추출 */
    public Authentication getAuthentication(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new ValidationException(BaseResponseStatus.INVALID_TOKEN);
        }

        try {
            // 토큰 파싱
            Claims claims = parseToken(token);

            // Auth 조회
            Integer authId = Integer.parseInt(claims.getSubject());
            Auth auth = authRepository.findById(authId)
                    .orElseThrow(() -> new ValidationException(BaseResponseStatus.USER_NOT_FOUND));
            CustomUserDetails userDetails = new CustomUserDetails(auth);
            Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                    claims.get("auth").toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        } catch (ExpiredJwtException e) {
            throw new ValidationException(BaseResponseStatus.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new ValidationException(BaseResponseStatus.INVALID_TOKEN);
        }
    }
}

