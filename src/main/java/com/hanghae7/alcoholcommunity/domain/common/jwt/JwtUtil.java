package com.hanghae7.alcoholcommunity.domain.common.jwt;

import com.hanghae7.alcoholcommunity.domain.common.jwt.TokenDto;
import com.hanghae7.alcoholcommunity.domain.common.security.RefreshToken;
import com.hanghae7.alcoholcommunity.domain.common.security.RefreshTokenRepository;
import com.hanghae7.alcoholcommunity.domain.common.security.UserDetailsServiceImplement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
	@Value("${jwt.secret.key}")
	private String secretKey;

	public static final String ACCESS_KEY = "ACCESS_KEY";
	public static final String REFRESH_KEY = "REFRESH_KEY";

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";

	private static final long ACCESS_TIME =  24 * 60 * 60 * 1000L;
	private static final long REFRESH_TIME = 14 * 24 * 60 * 60 * 1000L;


	private final UserDetailsServiceImplement userDetailsServiceImplement;
	private final RefreshTokenRepository refreshTokenRepository;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	private Key key;
	private RedisDao redisDao;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createToken(String memberUniqueId, String type) {
		Date date = new Date();
		long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

		return BEARER_PREFIX
			+ Jwts.builder()
			.setSubject(memberUniqueId)
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.setIssuedAt(date)
			.setExpiration(new Date(date.getTime() + time))
			.compact();
	}

	public TokenDto createAllToken(String memberUniqueId) {
		return new TokenDto(createToken(memberUniqueId, "Access"), createToken(memberUniqueId, "Refresh"));
	}

	public String resolveToken(HttpServletRequest httpServletRequest, String token) {
		String tokenName = token.equals("ACCESS_KEY") ? ACCESS_KEY : REFRESH_KEY;
		String bearerToken = httpServletRequest.getHeader(tokenName);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("유효하지 않은 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT token 입니다.");
			return false;
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.info("잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	public String getMemberInfoFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public Authentication createAuthentication(String memberUniqueId) {
		UserDetails userDetails = userDetailsServiceImplement.loadUserByUsername(memberUniqueId);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	public Boolean refreshTokenValidation(String token) {
		if (!validateToken(token)) return false;

		redisDao.getValues(getMemberInfoFromToken(token)).equals(token);
		// Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMemberEmailId(getMemberInfoFromToken(token));
		// return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
		return redisDao.getValues(getMemberInfoFromToken(token)).equals(token);
	}

	public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
		response.setHeader(ACCESS_KEY, accessToken);
	}
}
