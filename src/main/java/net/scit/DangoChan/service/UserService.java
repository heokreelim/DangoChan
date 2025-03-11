package net.scit.DangoChan.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.UserDTO;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {	
	// public memberVariable start
	
	// public memberVariable end
		
	// private memberVariable start
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	// private memberVariable end
	
	// PJB start
	// userId 에 해당하는 유저를 조회
	public UserDTO selectOne(Long userId) {
		Optional<UserEntity> temp = userRepository.findById(userId);
		
		if (!temp.isPresent()) {
			return null;
		}
		return UserDTO.toDTO(temp.get());
	}
	// PJB end
	
	// LHR start
	// 기본 회원가입 처리
	@Transactional
	public boolean registerUser(UserDTO dto)
	{
		try {
			UserEntity entity = UserEntity.toEntity(dto);
			
			if (userRepository.existsByEmail(entity.getEmail()))
			{
				// 이미 있는 계정은 가입불가처리
				// repository.save(entity)가 기존에 동일 id의 레코드가 존재할 경우 update처리 하므로 사전에 체크해서 처리
				return false;
			}
			else
			{
				// 비밀번호 암호화
				entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
				
				UserEntity savedEntity = userRepository.save(entity);
				
				// DB에 신규 회원정보 저장 성공여부 판단
				boolean isRegisterSucceeded = savedEntity != null && savedEntity.getUserId() != null;
				
				return isRegisterSucceeded;
			}
		} catch (Exception e) {
			log.info("===UserService Error==={}", e.getMessage());
			return false;
		}
	}
	
	// ID 중복체크
	public boolean idDuplCheck(String email) 
	{
		boolean isUserIdExist = userRepository.findByEmailAndAuthType(email, "LOCAL").isPresent();
		
		return !isUserIdExist; // 사용가능 여부는 등록된 유저가 없어야 하므로 ! 붙임
	}
	// LHR end
}
