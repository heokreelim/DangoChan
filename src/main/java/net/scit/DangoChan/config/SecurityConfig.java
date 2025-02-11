package net.scit.DangoChan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http
			.authorizeHttpRequests(
					(auth) -> auth
									// permit all
								  .requestMatchers(
									"/**"
							  	 ).permitAll()
			);
		/*
		// custom login
		http
			.formLogin((auth) -> auth
									 .loginPage("/user/login")
									 
									 .loginProcessingUrl("/user/loginProc")
									 
									 .usernameParameter("userId")
									 
									 .passwordParameter("userPwd")
									 
									 .defaultSuccessUrl("/")
									 
									 .successHandler(loginSuccessHandler)
									 
//									 .failureUrl("/user/login?error=true")
									 .failureHandler(loginFailureHandler)
									 
									 .permitAll()
					);
		
		// logout
		http
			.logout((auth) -> auth
								  .logoutUrl("/user/logout")
//								  .logoutSuccessUrl("/")
								  .logoutSuccessHandler(logoutSuccessHandler)
								  .invalidateHttpSession(true)
								  .clearAuthentication(true)
					);
		*/
		
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
