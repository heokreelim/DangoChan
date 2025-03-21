package net.scit.DangoChan.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.UserEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserDTO {
	private Long userId;
	private String email;
	private String password;
	private String userName;
	private String authType;
	private String providerId;
	private String roles;
	private LocalDateTime createdAt;
	private Integer profileImage;
	
	public static UserDTO toDTO(UserEntity entity)
	{
		return UserDTO.builder().userId(entity.getUserId())
								   .email(entity.getEmail())
								   .password(entity.getPassword())
								   .userName(entity.getUserName())
								   .authType(entity.getAuthType())
								   .providerId(entity.getProviderId())
								   .roles(entity.getRoles())
								   .createdAt(entity.getCreatedAt())
								   .profileImage(entity.getProfileImage())
								   .build();
	}
	
	// For OAuth2 login
	public static UserDTO toDTO(String userName, String authType, String providerId)
	{
		return UserDTO.builder().userName(userName)
								   .authType(authType)
								   .providerId(providerId)
								   .build();
	}
}
