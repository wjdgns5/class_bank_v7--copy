package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {
	
	private Integer id; 
	private String username; 
	private String password; 
	private String fullname; 
	private String orginFileName;
	private String uploadFileName;
	private Timestamp createdAt;
	
	// 이미지 파일명 작업?
	public String setUpUserImage() {
		return uploadFileName == null ? "https://picsum.photo/id/1/350" : "/images/uploads/" + uploadFileName;
	}
}
