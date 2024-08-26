package com.tenco.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.ToString;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@ToString
public class KakaoProfile {
	
	private Long id; // id
	private String connectedAt; // connected_at
	private Properties properties;
	
}
