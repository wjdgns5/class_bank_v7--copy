package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;

@Service // IoC 대상( 싱글톤으로 관리)
public class UserService {

	@Autowired
	private UserRepository userRepository;

//	@Autowired 어노테이션으로 대체 가능하다.
// 	생성자 의존 주입 - DI
//	public UserService(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	} // 생성자

	/**
	 * 회원 등록 서비스 기능 트랜잭션 처리
	 * 
	 * @param dto
	 */
	@Transactional
	public void createUser(SignUpDTO dto) {
		int result = 0; 
		try {
			result = userRepository.insert(dto.toUser());
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 처리입니다", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
		if(result != 1) {
			throw new DataDeliveryException("회원가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 로그인 서비스 기능 
	 * @param dto
	 * @return
	 */
	public User readUser(SignInDTO dto) {
		
		User userEntity = null;
		
		try {
			userEntity = userRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
		} catch (DataDeliveryException e) {
			throw new DataDeliveryException("잘못된 처리입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(userEntity == null) {
			throw new DataDeliveryException("아이디 혹은 비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST);
		}
		System.out.println("userEntity : " + userEntity.toString());
		
		return userEntity;
	}

	
}
