package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	// 계좌 생성 화면 요청 -DI 처리
	
	@Autowired
	private HttpSession session;
	
	// 여기에 AccountService 있어야 하는데 없음 ...  추후 만들예정
	@Autowired
	private AccountService accountService;
	
	/**
	 * 계좌 생성 페이지 요청
	 * 주소 설계 : http://localhost:8080/account/save
	 * @return
	 */
	@GetMapping("/save")
	public String savePage() {
		
		// 1. 인증 검사가 필요 ( account 전체가 필요하다.)
		User principal = (User) session.getAttribute("principal");
		
		if(principal == null) { // User 세션에 있는 값이 null 이라는 조건
			throw new UnAuthorizedException("인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}
		
		return "account/save";
	} // end of savePage()
	
	/**
	 * 계좌 생성 기능 요청
	 * 주소 설계 : http://localhost:8080/account/save
	 * @param dto
	 * @return 추후 계좌 목록 페이지 이동 처리
	 */
	@PostMapping("/save")
	public String savePage(SaveDTO dto) {
		// 1. form 데이터 추출 (계좌번호, 비밀번호, 계좌잔액?) 파싱전략
		// 2. 인증검사 --> User 세션 principal 에 있는 데이터 null 인지 비교
		// 3. 유효성 검사 --> form 데이터를 받았지만 이상하게 받았는지 아닌지 컨트롤러에서 처리
		// 4. 서비스 호출 --> 제대로된 데이터 양식을 받게되면 서비스에서 처리 후 컨트롤러로 다시 온다.
		
		User principal = (User) session.getAttribute("principal"); // 유저 세션 가져옴
		
		if(principal == null) { // 유저 세션을 통한 인증검사
			throw new UnAuthorizedException("인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}
		
		if(dto.getNumber() == null || dto.getNumber().trim().isEmpty()) {
			throw new DataDeliveryException("계좌 번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException("계좌 비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getBalance() == null || dto.getBalance() <= 0) {
			throw new DataDeliveryException("계좌 잔액을 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		
		accountService.createAccount(dto, principal.getId());
		
		return "redirect:/index";
	}
	
	
	
	
	
	
	
	
	
	
	

}
