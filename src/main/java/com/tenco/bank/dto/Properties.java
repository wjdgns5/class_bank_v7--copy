package com.tenco.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.ToString;

@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public class Properties {
	
	private String nickname; // nickname
	private String profileImage; // profile_image
	private String thumbnailImage; // thumbnail_image

}
