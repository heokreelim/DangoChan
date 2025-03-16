package net.scit.DangoChan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.handler.LoginFailureHandler;
import net.scit.DangoChan.handler.OAuth2AuthenticationSuccessHandler;
import net.scit.DangoChan.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final LoginFailureHandler loginFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http
			.oauth2Login(
	                configurer -> configurer
	                        .defaultSuccessUrl("/home", true) // 로그인에 성공 시 /home 경로로 리다이렉트
	                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 사용자 정보 매핑 서비스 등록
                            )
	                        .successHandler(new OAuth2AuthenticationSuccessHandler())
			)
			.authorizeHttpRequests(
					(auth) -> auth
								// WebSocket 및 채팅 관련, 정적 리소스 허용
								// .requestMatchers("/ws-stomp/**", "/chat/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**").permitAll()
								.requestMatchers(
										// 홈
										"/", "/home"
										// 로그인
										, "/user/join", "/user/joinProc", "/user/idCheck"
										, "/user/login", "/user/loginProc", "/user/logout"
										, "/user/guestlogin"
										, "/oauth2/authorization/**", "/login/oauth2/code/**"
										// static resources
										, "/images/**", "/js/**", "/css/**"
										// community board
										, "/community/communityBoardList"
										// Chat 관련
										, "/ws-stomp/**", "/webjars/**", "/uploads/**"
									).permitAll()
								
								// admin role
								.requestMatchers("/admin").hasRole("ADMIN")
							  
								// admin, user role
								.requestMatchers("/user/mypage").hasAnyRole("ADMIN", "USER")
								
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
									 
									 .defaultSuccessUrl("/home")
									 
									 //.successHandler(loginSuccessHandler)
									 
									 //.failureUrl("/user/login?error=true")
									 .failureHandler(loginFailureHandler)
									 
									 .permitAll()
					);
		
		// logout
		http
			.logout((auth) -> auth
								  .logoutUrl("/user/logout")
								  .logoutSuccessUrl("/user/login")
								  //.logoutSuccessHandler(logoutSuccessHandler)
								  .invalidateHttpSession(true)
								  .clearAuthentication(true)
					);
		
		http
		    .sessionManagement(session -> session
		        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ✅ 세션 유지 설정
		    )
		    
		    .securityContext(security -> security
		            .requireExplicitSave(false)) // ✅ SecurityContext 자동 저장 활성화
		    
		    .exceptionHandling(exception -> exception
		        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/user/login")) // 로그인 페이지 변경
		    )
		    
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
