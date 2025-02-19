package net.scit.DangoChan.handler;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
//		super.onAuthenticationFailure(request, response, exception);
		
		String errMessage = "";
		
//		log.info("======= login error: {}", exception.getMessage());
		
		if (exception instanceof BadCredentialsException)
		{
			errMessage += "아이디나 비번이 잘못되었습니다.";
			errMessage += exception.getMessage();
			log.info("======= BadCredentialsException", exception.getMessage());
		}
		else
		{
			errMessage += "로그인 실패. 관리자에게 문의 요망";
			errMessage += exception.getMessage();			
			log.info("======= else error: {}", exception.getMessage());
		}
		
		errMessage = URLEncoder.encode(errMessage, "UTF-8");
		
		response.sendRedirect("/user/login?error=true&errMessage=" + errMessage);
	}
}
