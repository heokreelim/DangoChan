package net.scit.DangoChan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				// WebSocket 및 채팅 관련, 정적 리소스 허용
//				.requestMatchers("/ws-stomp/**", "/chat/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**").permitAll()
				.requestMatchers("/**").permitAll()
				// 그 외 모든 요청은 인증 필요 (원하는 대로 설정)
				.anyRequest().authenticated()
		);

		// (옵션) 로그인/로그아웃 설정은 생략
		http.csrf(csrf -> csrf.disable());

		return http.build();
	}
}
