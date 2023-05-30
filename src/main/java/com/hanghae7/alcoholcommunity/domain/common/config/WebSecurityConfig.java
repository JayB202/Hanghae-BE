package com.hanghae7.alcoholcommunity.domain.common.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.hanghae7.alcoholcommunity.domain.common.jwt.JwtAuthFilter;
import com.hanghae7.alcoholcommunity.domain.common.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig {


	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF 설정
		http.csrf().ignoringAntMatchers("/ws-stomp/**").disable(); // 웹소켓 경로에 대한 CSRF 비활성화
		http.cors();
		// 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeHttpRequests(authorize -> authorize
				.shouldFilterAllDispatcherTypes(false)
				.antMatchers("/swagger-ui/**").permitAll()
				.antMatchers("/v3/api-docs/**").permitAll()
				.antMatchers("/**").permitAll()
				.anyRequest()
				.authenticated());// 그외의 요청들은 모두 인가 받아야 한다.
			// JWT 인증/인가를 사용하기 위한 설정
		// 401 에러 핸들링
		//SockJS를 위해
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.addAllowedOrigin("http://localhost:3000");
		config.addAllowedOrigin("http://localhost:8080");
		config.addAllowedOrigin("http://13.209.47.171:8080");
		config.addAllowedOrigin("https://soolo-fe.vercel.app");
		config.addExposedHeader(JwtUtil.AUTHORIZATION_HEADER);
		config.addExposedHeader(JwtUtil.ACCESS_KEY);
		config.addExposedHeader(JwtUtil.REFRESH_KEY);
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");
		config.setAllowCredentials(true);
		config.validateAllowCredentials();

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}

}
