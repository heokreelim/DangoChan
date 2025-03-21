package net.scit.DangoChan.service;

import java.util.NoSuchElementException;
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
	
	// 유저 프로필 번호 변경 
	public boolean updateProfileImage(Long userId, Integer profileImageNumber) {

	    // Null 체크는 Controller에서 이미 처리됨, 여기서는 단순 업데이트만 수행
	    int result = userRepository.updateProfileImage(userId, profileImageNumber);

	    // result 값이 1이면 정상 업데이트, 0이면 실패
	    return result > 0;
	}
	
	// userId 로 User 조회
	public UserDTO findUserById(Long userId) {
	    UserEntity userEntity = userRepository.findById(userId)
	                                          .orElseThrow(() -> new RuntimeException("사용자 없음"));
	    return UserDTO.toDTO(userEntity);
	}
	
	
	// 유저 현재 프로필 번호 조회
	public Integer getCurrentProfileImage(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return user.getProfileImage();
    }
	
	public UserEntity findById(Long userId) {
	    return userRepository.findById(userId)
	            .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다. userId=" + userId));
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
	
	// 닉네임 변경
	public boolean editNickname(Long userId, String newNickName)
	{
		try {
			Optional<UserEntity> entityOp = userRepository.findById(userId);
			
			if (entityOp.isPresent())
			{
				UserEntity entity = entityOp.get();
				entity.setUserName(newNickName);
				
				UserEntity savedEntity = userRepository.save(entity);
				
				boolean nickNameChangeSucceeded = savedEntity != null && savedEntity.getUserId() != null;
				
				return nickNameChangeSucceeded;
			}
			else
			{
				log.info("======= 닉네임 변경 실패: 유저({})가 존재하지 않습니다.", userId);
				return false;				
			}
		} catch (Exception e) {
			log.info("======= UserService editNickname Error: {}", e.getMessage());
			return false;
		}
	}
	// LHR end
}
