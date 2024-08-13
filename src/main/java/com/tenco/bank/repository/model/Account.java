package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.utils.Define;

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
public class Account {
	
	private Integer id;
	private String number;
	private String password;
	private Long balance;
	private Integer userId;
	private Timestamp createdAt;
	
	// 출금 기능 
	public void withdraw(Long amount) {
		this.balance = balance - amount;
	}
	
	// 입금 기능
	public void deposit(Long amount) {
		this.balance += amount; 
	}
	
	// 패스워드 체크 기능
	public void checkPassword(String password) {
		if(this.password.equals(password) == false) {
			throw new DataDeliveryException(Define.FAIL_ACCOUNT_PASSWROD, HttpStatus.BAD_REQUEST);
		}
	}

	// 잔액 여부 확인 기능
	public void checkBalance(Long amount) {
		if(this.balance < amount) {
			throw new DataDeliveryException(Define.LACK_Of_BALANCE, HttpStatus.BAD_REQUEST);
		}
	}
		
	// 계좌 소유자 확인 기능 (세션 확인 해서?)
	public void checkOwner(Integer principalId) {
		if(this.userId != principalId) {
			throw new DataDeliveryException(Define.NOT_ACCOUNT_OWNER, HttpStatus.BAD_REQUEST);
		}
	}
}
