package net.scit.DangoChan.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class LoginUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private String email;
	private String password;
	private String userName;
	private String authType;
	private String roles;
	private LocalDateTime createdAt;
	
	// 유저가 가진 Role 을 콜렉션으로 만들어 반환. 보통 List 사용
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(roles));
	}
	
	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() { // spring 에서는 Username이 유저의 ID를 뜻한다.
		return this.email;
	}
	
	// 사용자정의 Getter
	public String getUserName()
	{
		return this.userName;
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
								.build();
	}
}