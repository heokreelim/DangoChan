package net.scit.DangoChan.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {
	private final UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// userId가 실제 테이블에서도 Id(즉, primary key)로 취급되므로 findById 사용
		Optional<UserEntity> entityOp = repository.findByEmail(email);
		
		if(entityOp.isPresent())
		{
			return LoginUserDetails.toUserDetail(entityOp.get());
		}
		else
		{
			throw new UsernameNotFoundException("로그인 시도 중 Error발생");
		}
	}
}
