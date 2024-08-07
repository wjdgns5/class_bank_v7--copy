package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.handler.exception.UnAuthorizedException;

@Controller
public class mainController {

	// REST API  기반으로 주소설계 가능 
	
		// 주소설계 
		// http:localhost:8080/main-page
		// http:localhost:8080/index
		@GetMapping({"/main-page", "/index"})
//		@ResponseBody
		public String mainPage() {
			System.out.println("mainPage() 호출 확인");
			// [JSP 파일 찾기 (yml 설정) ] - 뷰 리졸버 
			// prefix: /WEB-INF/view
			//         /main  
			// suffix: .jsp
			return "main";
		}	
		
		// TODO- 주소 설계
		// 주소 설계
		// http://localhost:8080/error-test1/true
		// http://localhost:8080/error-test1/isError
		// http://localhost:8080/error-test1
		
		@GetMapping("/error-test1")
		public String errorPage() {
			System.out.println("111111111111");
			
			if(true) {
				throw new RedirectException("잘못된 요청입니다.", HttpStatus.NOT_FOUND); // 404
			}
			
			return "main";
		}
		
		// http://localhost:8080/error-test2
		@GetMapping("/error-test2")
		public String errorData2() {
			System.out.println("2222222222222");
			
			if(true) {
				throw new DataDeliveryException("잘못된 데이터 입니다.", HttpStatus.BAD_REQUEST); // 400
			}
			
			return "main";
		}
		
		// http://localhost:8080/error-test3
		@GetMapping("/error-test3")
		public String errorData3() {
			System.out.println("33333333333333");
			
			if(true) {
				throw new UnAuthorizedException("인증 안된 사용자 입니다.", HttpStatus.UNAUTHORIZED); // 401
			}
			return "main";
			
		}

}
