package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UnAuthorizedException extends RuntimeException {
// 클래스는 인증이 안된 사용자가 인증이 필요하 서비스에 접근 요청을 할 때
// 예외를 발생 시킬 사용자 정의 예외 클래스를 설계 합니다. 
	
// 인증이 필요한 서비스에 인증안된 사용자가 접근 시 예외발생 (사용자 정의 예외)
	
	private HttpStatus status;
	
	// throw new UnAuthorizedException(   ,  ) 
	public UnAuthorizedException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
	
	
}
