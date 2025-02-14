package net.scit.DangoChan.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name="users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long userId;
	
	@Column(name="email", nullable = false, unique=true)
	private String email;
		
	@Column(name="password", nullable = false)
	private String password;
	
	@Column(name="user_name", nullable = false, unique=true)
	private String userName;
	
	@Column(name="auth_type", nullable = false)
	private String authType;
	
	@Column(name="provider_id")
	private String provider_id;
	
	@Column(name="create_date")
	@CreationTimestamp
	private LocalDateTime createdAt;
}
