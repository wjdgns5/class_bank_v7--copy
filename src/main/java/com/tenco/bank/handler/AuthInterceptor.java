package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// preHandle 동작 흐름 (단 : 스프링부트 설정파일, 성정 클래스에 등록되어야 한다, : 특정 URL)

@Component //  Class를 Bean으로 등록하기 위한 Annotation / Bean 이란 ? : 스프링 컨테이너에 의해 관리되는 재사용 가능한 소프트웨어 컴포넌트
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// 사용자 정보 유추하는 방법?
		HttpSession session = request.getSession();
		User princiapl = (User)session.getAttribute("principal");
		
		if(princiapl == null) {
			throw new UnAuthorizedException("로그인 먼저 해 주세요.", HttpStatus.UNAUTHORIZED);
		}
		return true;
	}

	// postHandle
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	} // HandlerInterceptor : 특정한 URI 호출을 '가로채는' 역할
	
	
	
	
	
}
