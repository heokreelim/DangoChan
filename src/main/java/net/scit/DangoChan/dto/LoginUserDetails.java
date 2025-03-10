package net.scit.DangoChan.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.UserEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class LoginUserDetails implements OAuth2User, UserDetails {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
	private Long userId;
	private String email;
	private String password;
	private String userName;
	private String authType;
	private String roles;
	private LocalDateTime createdAt;
	
	// OAuth2User
	private Map<String, Object> attributes;
	private String providerId;
	
	// 유저가 가진 Role 을 콜렉션으로 만들어 반환. 보통 List 사용
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() 
	{
		return List.of(new SimpleGrantedAuthority(roles));
	}
	
	@Override
	public String getPassword() 
	{
		return this.password;
	}

	@Override
	public String getUsername() // spring 에서는 Username이 유저의 ID를 뜻한다.
	{ 
		switch (this.authType)
		{
			case "LOCAL":
			{
				return this.email;
			}
			
			default:
				return this.providerId;
		}
	}
	
	// 사용자정의 Getter
	public String getUserName()
	{
		return this.userName;
	}
	
	// OAuth2User implementation ========
	@Override
	public Map<String, Object> getAttributes() 
	{
		return attributes;
	}

	// OAuth2User implementation ========
	@Override
	public String getName() {
		return this.getAttribute(this.providerId).toString();
	}
	
	public static LoginUserDetails toUserDetail(UserEntity entity)
	{
		return LoginUserDetails.builder()
								.userId(entity.getUserId())
								.email(entity.getEmail())
								.password(entity.getPassword())
								.userName(entity.getUserName())
								.authType(entity.getAuthType())
								.roles(entity.getRoles())
								.createdAt(entity.getCreatedAt())
								.providerId(entity.getProviderId())
								.build();
	}
	
	public static LoginUserDetails toUserDetail(UserEntity entity
							, Map<String, Object> attributes, String providerId)
	{
		return LoginUserDetails.builder()
								.userId(entity.getUserId())
								.email(entity.getEmail())
								.password(entity.getPassword())
								.userName(entity.getUserName())
								.authType(entity.getAuthType())
								.roles(entity.getRoles())
								.createdAt(entity.getCreatedAt())
								.attributes(attributes)
								.providerId(providerId)
								.build();
	}
}