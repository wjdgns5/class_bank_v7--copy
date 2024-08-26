package com.tenco.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.ToString;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@ToString
public class OAuthToken {
// 토큰 받기 - 응답 
    private String accessToken; // access_token : 토큰 타입, bearer로 고정

    private String tokenType; // token_type : 사용자 액세스 토큰 값

    private String refreshToken; // refresh_token : 사용자 리프레시 토큰 값

    private Integer expiresIn; // expires_in : 액세스 토큰과 ID 토큰의 만료 시간(초)
  
    private String scope; // scope : 인증된 사용자의 정보 조회 권한 범위

    private Integer refreshTokenExpiresIn; // refresh_token_expires_in : 리프레시 토큰 만료 시간(초)
}
