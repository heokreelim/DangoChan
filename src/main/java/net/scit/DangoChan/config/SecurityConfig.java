package net.scit.DangoChan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.handler.LoginFailureHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final LoginFailureHandler loginFailureHandler;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		
		http
			.authorizeHttpRequests(
					(auth) -> auth
								// WebSocket 및 채팅 관련, 정적 리소스 허용
								// .requestMatchers("/ws-stomp/**", "/chat/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**").permitAll()
								.requestMatchers("/**").permitAll()
								// permit all
								.requestMatchers(
									"/**"
							  	).permitAll()
								// 그 외 모든 요청은 인증 필요 (원하는 대로 설정)
								.anyRequest().authenticated()
			);
		
		
		// custom login
		http
			.formLogin((auth) -> auth
									 .loginPage("/user/login")
									 
									 .loginProcessingUrl("/user/loginProc")
									 
									 .usernameParameter("email")
									 
									 .passwordParameter("password")
									 
									 .defaultSuccessUrl("/")
									 
									 //.successHandler(loginSuccessHandler)
									 
									 //.failureUrl("/user/login?error=true")
									 .failureHandler(loginFailureHandler)
									 
									 .permitAll()
					);
		
		// logout
		http
			.logout((auth) -> auth
								  .logoutUrl("/user/logout")
								  .logoutSuccessUrl("/")
								  //.logoutSuccessHandler(logoutSuccessHandler)
								  .invalidateHttpSession(true)
								  .clearAuthentication(true)
					);
		
		
		http
			.csrf((auth) -> auth.disable());

		return http.build();
	}
	
	// 비밀번호 암호화
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
}
