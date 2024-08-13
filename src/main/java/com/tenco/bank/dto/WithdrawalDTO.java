package com.tenco.bank.dto;

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
public class WithdrawalDTO {
	
	private Long amount; // 출금금액
	private String wAccountNumber; // 출금 계좌 번호
	private String wAccountPassword; // 출금 계좌 비밀 번호

}
