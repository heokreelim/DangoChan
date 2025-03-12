package net.scit.DangoChan.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;

	@Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("======== CustomOAuth2UserService 호출됨!");
				
		OAuth2User oauth2User = super.loadUser(userRequest);
		
		log.info("======== API 응답: {}", oauth2User.getAttributes());

        if (oauth2User.getAttributes().isEmpty()) {
            throw new OAuth2AuthenticationException("API에서 사용자 정보를 가져오지 못함.");
        }
        
        // 어떤 OAuth2 제공자인지 확인
 		String provider = userRequest.getClientRegistration().getRegistrationId();

 		log.info("======== provider: {}", provider);
        
        // LINE 사용자 정보 매핑
        Map<String, Object> attributes = oauth2User.getAttributes();

        String providerId;
        String displayName;
        
        if ("line".equals(provider)) 
        {
            providerId = (String) attributes.get("userId"); // LINE userId
            displayName = (String) attributes.get("displayName");
        } 
        else if ("google".equals(provider)) 
        {
            providerId = (String) attributes.get("sub"); // Google sub (고유 ID)
            displayName = (String) attributes.get("name"); // Google 이름
        } 
        else 
        {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + provider);
        }

        if (providerId == null) {
            throw new OAuth2AuthenticationException(provider.toUpperCase() + " API에서 providerId를 찾을 수 없습니다.");
        }
        else
        {
        	Optional<UserEntity> entityOp = userRepository.findByAuthTypeAndProviderId(provider.toUpperCase(), providerId);

            if (entityOp.isPresent()) 
            { 
            	// 기존 사용자 로그인
                UserEntity entity = entityOp.get();
                log.info("======== {} 로그인: {} (이미 등록된 회원)", provider.toUpperCase(), providerId);
                return LoginUserDetails.toUserDetail(entity, attributes, providerId);
            } 
            else 
            { 
            	// 신규 회원 등록
                UserEntity registeredEntity = registerOAuth2User(UserDTO.toDTO(displayName, provider.toUpperCase(), providerId));
                if (registeredEntity != null) {
                    log.info("========== {} 회원 등록 성공", provider.toUpperCase());
                    return LoginUserDetails.toUserDetail(registeredEntity, attributes, providerId);
                } else {
                    log.error("========== {} 회원 등록 실패", provider.toUpperCase());
                    throw new OAuth2AuthenticationException("OAuth2 회원 등록 실패");
                }
            }
        }
    }
	
	// OAuth2.0 회원가입 처리 - Google, Line
	@Transactional
	public UserEntity registerOAuth2User(UserDTO dto)
	{
		try {
			UserEntity entity = UserEntity.toEntity(dto);

			UserEntity savedEntity = userRepository.save(entity);
			
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
			log.error("========== CustomOAuth2UserService Error : {}", e.getMessage());
			return null;
		}
	}
}