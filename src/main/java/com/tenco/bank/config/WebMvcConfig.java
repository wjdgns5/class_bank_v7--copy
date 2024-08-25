package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration // 하나 이상의 bean 등록
@RequiredArgsConstructor // 생성자 대신에 사용할 수 있음
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private final AuthInterceptor authInterceptor;

	// 우리가 만들어 놓은 AuthInterceptor를 등록해야 한다.
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
			.addPathPatterns("/account/**")
			.addPathPatterns("/auth/**");
	}
	
	/**
	 * 코드추가
	 * C:\Users\GGG\Documents\Lightshot\a.png <-- 서버 컴퓨터상에 실제 이미지 경로지만
	 * 프로젝트 상에서 (클라이언트가 HTML 소스로 보이는 경로는) /images/uploads/**
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/uploads/**")
		.addResourceLocations("file:\\C:\\work_spring\\upload/");
	}

	@Bean // IoC 대상 (싱글톤 처리)
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
