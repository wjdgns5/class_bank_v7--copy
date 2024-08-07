package com.tenco.bank.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.handler.exception.UnAuthorizedException;

@ControllerAdvice // MVC 컨트롤러 에서 발생하는 예외를 처리하기 위해 사용
public class GlobalControllerAdvice {
	// MVC 컨트롤러 에서 발생하는 예외를 처리하기 위해 사용
	
	public GlobalControllerAdvice() {
		
	} // 생성자
	
	/**
	 * (개발시에 많이 활용)
	 * 모든 예외 클래스를 알 수 없기 때문에 로깅으로 확인할 수 있도록 설정 
	 * 
	 * @Slf4j : 에러 로깅을 위한 어노테이션 (비동기 처리 된다.)
	 * @ExceptionHandler(xxx.class) : xxx.class 익셉션이 발생할 경우 캐치를 하겠다는 뜻이다
	 * 
	 */
	@ExceptionHandler(Exception.class)
	public void exception(Exception e) {
		System.out.println("------------------");
		System.out.println(e.getClass().getName());
		System.out.println(e.getMessage());
		System.out.println("------------------");
	}
	
	
	// @ResponseBody : 자바 객체를 HttpResponse의 본문 responseBody의 내용으로 매핑하는 역할
	// @ControllerAdvice : MVC 컨트롤러 에서 발생하는 예외를 처리하기 위해 사용
	// @RestControllerAdvice : @ResponseBody + @ControllerAdvice
	
	// 예외를 내릴 때 데이터를 내리고 싶다면. @RestControllerAdvice 를 사용한다.
	// 단, @ControllerAdvice 사용하고 있다면 @ResponseBody를 붙여서 사용하면 된다.
	
	@ResponseBody
	@ExceptionHandler(DataDeliveryException.class)
	public String dataDeleveryException(DataDeliveryException e) {
		// 문자열 <-- 멀티스레드 
		StringBuffer sb = new StringBuffer();
		sb.append(" <script>");
		sb.append(" alert('"+ e.getMessage() +"');");
		sb.append(" window.history.back();"); // 뒤로가기
		sb.append(" </script>");
		
		return sb.toString();
	}
	
	@ResponseBody
	@ExceptionHandler(UnAuthorizedException.class)
public String unAuthorizedException(UnAuthorizedException e) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(" <script>");
		sb.append(" alert('"+ e.getMessage() +"');");
		sb.append(" window.history.back();"); // 뒤로가기
		sb.append(" </script>");
		
		return sb.toString();
	} // 인증 안된 사용자 에게
	
	/**
	 * 에러 페이지로 이동 처리
	 * JSP로 이동시 데이터를 담아서 보내는 방법 
	 * ModelAndView, Model 사용 가능 
	 * throw new RedirectException('페이지 없는데???', 404);
	 */
	
	@ExceptionHandler(RedirectException.class)
	public ModelAndView redirectException(RedirectException e) {
		ModelAndView modelAndView = new ModelAndView("errorPage");
		modelAndView.addObject("statusCode", e.getStatus().value());
		modelAndView.addObject("message", e.getMessage());
		return  modelAndView; // 페이지 반환 + 데이터 내려줌 
	}
	
	
	

}
