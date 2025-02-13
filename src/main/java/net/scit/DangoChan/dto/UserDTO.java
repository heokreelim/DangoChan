package net.scit.DangoChan.dto;

import java.time.LocalDateTime;

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
public class UserDTO {
	private Long userId;
	private String email;
	private String password;
	private String userName;
	private String authType;
	private LocalDateTime createdAt;
}
