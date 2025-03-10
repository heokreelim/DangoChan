package net.scit.DangoChan.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.dto.UserDTO;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {
	private final UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException 
	{
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
	
	public LoginUserDetails loadUserByGuestLoginKey(UUID guestLoginKey) throws UsernameNotFoundException 
	{
		LoginUserDetails userDetails;
		String provider = "GUEST";		
		
		// guest login 처음 -> 회원가입 처리
		if (guestLoginKey == null) 
		{
			UUID newGuestLoginKey = UUID.randomUUID();
			guestLoginKey = newGuestLoginKey;
			log.info("=========== guset login / first / {}", guestLoginKey.toString());
			
			long nextUserNum = repository.countGuestUsersWithNumbers();
			String tempUserName = "Guest_" + (nextUserNum + 1);
			
			UserEntity registeredEntity = registerGuestUser(UserDTO.toDTO(tempUserName, provider, guestLoginKey.toString()));
            if (registeredEntity != null) {
                log.info("========== 게스트 회원 등록 성공 : {}", registeredEntity.getProviderId());
            } else {
                log.info("========== 게스트 회원 등록 실패");
                throw new UsernameNotFoundException("게스트 등록 실패");
            }
            
            userDetails = LoginUserDetails.toUserDetail(registeredEntity);
		}
		else
		{
			log.info("=========== guset login / again / {}", guestLoginKey.toString());
			
			Optional<UserEntity> entityOp = repository.findByAuthTypeAndProviderId(provider, guestLoginKey.toString());
			if (entityOp.isPresent()) 
            {
				userDetails = LoginUserDetails.toUserDetail(entityOp.get());
				log.info("========== 기존 게스트 회원 확인 성공 : {}", userDetails.getProviderId());
            }
			else
			{
				log.info("========== 게스트 회원 확인 실패");
				throw new UsernameNotFoundException("등록되지 않은 GuestID!");
			}
		}

		Authentication authentication = new UsernamePasswordAuthenticationToken((UserDetails)userDetails, null, userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
//		SecurityContextHolder.setContext(context);
		
		log.info("========== guset login / userDetail 세션 등록 성공");
		
        return userDetails;
    }
	
	// Guest Login User 회원데이터 생성
	@Transactional
	public UserEntity registerGuestUser(UserDTO dto)
	{
		try {
			UserEntity entity = UserEntity.toEntity(dto);

			UserEntity savedEntity = repository.save(entity);
			
			// DB에 신규 회원정보 저장 성공여부 판단
			if (savedEntity != null && savedEntity.getUserId() != null)
			{
				return savedEntity;				
			}
			else
			{
				return null;
			}
		} catch (Exception e) {
			log.error("========== registerGuestUser Error : {}", e.getMessage());
			return null;
		}
	}
}
