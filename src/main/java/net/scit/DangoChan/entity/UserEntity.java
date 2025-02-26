package net.scit.DangoChan.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.dto.UserDTO;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Data
@Table(name="users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long userId;
	
	@Column(name="email", unique=true)
	private String email;
		
	@Column(name="password")
	private String password;
	
	@Column(name="user_name", nullable = false, unique=true)
	private String userName;
	
	@Column(name="auth_type", nullable = false)
	private String authType;
	
	@Column(name="provider_id")
	private String providerId;
	
	@ColumnDefault("ROLE_USER")
	@Column(name="roles")
	private String roles;
	
	@Column(name="created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	// roles 기본값 지정을 위해 메서드 선언
	@PrePersist
	private void setDefaultRoles()
	{
		if (this.roles == null)
			this.roles = "ROLE_USER";
	}
	
	@OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    @ToString.Exclude	// UserEntity와 CategoryEntity의 toString() 무한 호출문제 해결
    private List<CategoryEntity> categoryEntityList;  // userEntity(1) -> categoryEntity(N)

	
	
	public static UserEntity toEntity(UserDTO dto)
	{
		return UserEntity.builder().userId(dto.getUserId())
								   .email(dto.getEmail())
								   .password(dto.getPassword())
								   .userName(dto.getUserName())
								   .authType(dto.getAuthType())
								   .providerId(dto.getProviderId())
								   .roles(dto.getRoles())
								   .build();
	}
}
