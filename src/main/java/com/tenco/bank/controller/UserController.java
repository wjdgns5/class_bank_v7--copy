package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.tenco.bank.dto.KakaoProfile;
import com.tenco.bank.dto.OAuthToken;
import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController { 
	
	@Autowired // DI 처리 --> 의존성 주입
	private UserService userService;
	
	@Autowired // DI 처리 --> 의존성 주입
	private  HttpSession session; // 세션 준비'
	
	@Value("${tenco.key}")
	private String tencoKey; 
	
	/**
	 * 회원 가입 페이지 요청
	 * 주소 설계 : http://localhost:8080/user/sign-up
	 * @return
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		
		// prefix: /WEB-INF/view/
		// return: user/signUp
		// suffix: .jsp 
		
		return "user/signUp";
	} // end of signUpPage()
	
	/**
	 * 회원 가입 로직 처리 요청
	 * 주소 설계 : http://localhost:8080/user/sign-up
	 * @param dto
	 * @return
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpDTO dto) {
		// SignUpDTO : SignUpDTO(username=이, password=asd123, fullname=순신)
		System.out.println("SignUpDTO : " + dto.toString()); // SignUpDTO 의 값을 toString으로 받는다.
		
		// 1. 인증검사 
		if(dto.getUsername() == null || dto.getUsername().trim().isEmpty()) { 
			// 유저 이름이 null 이거나 유저의 이름이 공백이 있거나 비어있는 경우
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}
		
		// 2. 인증검사
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			// 유저 비밀번호가 null 이거나 유저 비밀번호가 공백이 들어가있거나 비어있는 경우
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		// 3. 인증검사
		if(dto.getFullname() == null || dto.getFullname().trim().isEmpty()) {
			// 유저 풀 네임이 null 이거나 유저 풀 네임에 공백이 들어가있거나 비어있는 경우
			throw new DataDeliveryException(Define.ENTER_YOUR_FULLNAME, HttpStatus.BAD_REQUEST);
		}
		
		// 유저 서비스의 회원 가입 등록 서비스 기능을 실행한다.
		userService.createUser(dto);
		
	//	return "redirect:/index";
		return "redirect:/user/sign-in";
	} // end of signUpProc()
	
	/**
	 * 로그인 페이지 이동
	 * 주소 설계 : http://localhost:8080/user/sign-in
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		
		return "/user/signIn";
	} // end of signInPage()
	
	/**
	 * 로그인 기능 설계
	 * @param dto
	 * @return
	 */
	@PostMapping("/sign-in")
	public String signProc(SignInDTO dto) {
		if(dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		} 
		
		if(dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		System.out.println("SignInDTO : " + dto.toString());
		
		User principal = userService.readUser(dto);
		
		session.setAttribute(Define.PRINCIPAL, principal);
		System.out.println("principal : " + principal.toString());
		
		return "redirect:/account/list";
	}
	
	/**
	 * 로그아웃 기능 설계
	 * @return
	 */
	@GetMapping("/logout")
	public String logout() {
		
		session.invalidate(); // 세션 소멸
		
		return "redirect:/user/sign-in";
	}
	
	@GetMapping("/kakao")
//	@ResponseBody
	public String getMethodName(@RequestParam(name = "code") String code) {
		System.out.println("code : " + code);
		
		RestTemplate rt1 = new RestTemplate();
		
		// 헤더 구성
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 바디 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "4338cd3d11df8f8f1654fff2e37bd659");
		params.add("redirect_uri", "http://localhost:8080/user/kakao");
		params.add("code", code);
		
		// 헤더 + 바디 결합
		HttpEntity<MultiValueMap<String, String>> reqKaKaoMessage = new HttpEntity<>(params, headers);
		
		// 통신 요청 (토큰 받기)
//		ResponseEntity<String> response = rt1.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST ,reqKaKaoMessage, String.class);
//		System.out.println("response : " + response.getBody().toString());
//		return response.getBody().getAccessToken();
		
		// 통신 요청 (토큰 응답)
		ResponseEntity<OAuthToken> response = rt1.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST ,reqKaKaoMessage, OAuthToken.class);
		System.out.println("response : " + response.getBody().toString());
		
		// -----------------------------------------------------------------
		
		// (카카오 리소스 서버 사용자 정보 가져오기)
		RestTemplate rt2 = new RestTemplate();
		
		//헤더 설정
		HttpHeaders headers2 = new HttpHeaders();	
		
		headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 본문 x --> get 방식으로 받음
		
		// 헤더 본문 결합?
		HttpEntity<MultiValueMap<String, String>> reqKakaoInfoMessage = new HttpEntity<>(headers2);
		
		// 통신 요청 (사용자 정보 가져오기 JSON 형식)
//		ResponseEntity<String> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, reqKakaoInfoMessage, String.class);
//		System.out.println("kakaoProfile : " + response2.getBody());
//		return response2.getBody();
		
		// 통신 요청 
	    ResponseEntity<KakaoProfile> resposne2 = 
	    			rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, 
	    					reqKakaoInfoMessage, KakaoProfile.class);
	    
	    KakaoProfile kakaoProfile = resposne2.getBody();
	    // ---- 카카오 사용자 정보 응답 완료 ----------
	    
	    // 최초 사용자라면 자동 회원 가입 처리 (우리 서버) 
	    // 회원가입 이력이 있는 사용자라면 바로 세션 처리 (우리 서버) 
	    // 사전기반 --> 소셜 사용자는 비밀번호를 입력하는가? 안하는가? 
	    // 우리서버에 회원가입시에 --> password -> not null (무건 만들어 넣어야 함 DB 정책) 
	    
	    // 1.회원가입 데이터 생성 
	    SignUpDTO signUpDTO = SignUpDTO.builder()
	    		.username(kakaoProfile.getProperties().getNickname() 
	    				+ "_" + kakaoProfile.getId())
	    		.fullname("OAuth_" + kakaoProfile.getProperties().getNickname())
	    		.password(tencoKey)
	    		.build();
	    System.out.println("signUpDTO.toString() : " + signUpDTO.toString());
	   
	    // 2.우리사이트 최초 소셜 사용자 인지 판별 
	    User oldUser = userService.searchUsername(signUpDTO.getUsername());
	    // select * from user_tb where username = ?
	 
	    if(oldUser == null) {
	    	// 사용자가 최초 소셜 로그인 사용자 임
	    	oldUser = new User();
	    	oldUser.setUsername(signUpDTO.getUsername());
	    	oldUser.setPassword(null);
	    	oldUser.setFullname(signUpDTO.getFullname());
	    	oldUser.setOrginFileName(signUpDTO.getOriginFileName()); // 추가
	    	oldUser.setUploadFileName(signUpDTO.getUploadFileName()); // 추가
	    	userService.createUser(signUpDTO);
	    }
	    
	    // 프로필 이미지 여부에 따라 조건식 추가 
    	signUpDTO.setOriginFileName(kakaoProfile.getProperties().getThumbnailImage());
    	oldUser.setUploadFileName(kakaoProfile.getProperties().getThumbnailImage());
    	
    	System.out.println("@@: " + kakaoProfile.getProperties().getThumbnailImage());
    	System.out.println("@@@ : " + kakaoProfile.getProperties().getThumbnailImage());
	    
	    // 자동 로그인 처리
	    session.setAttribute(Define.PRINCIPAL, oldUser);
		return "redirect:/account/list";
	}
	
	

}
