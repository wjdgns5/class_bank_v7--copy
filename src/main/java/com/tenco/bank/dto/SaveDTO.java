package com.tenco.bank.dto;

import com.tenco.bank.repository.model.Account;

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
public class SaveDTO {
	
	private String number; // 계좌번호 - << 이런거 들어가서 String으로 했음
	 
	private String password; // 비밀번호 << 만약에 암호화 했을 시 문자열로 변환되기 때문에 
	
	private Long balance; // 계좌잔액 << sql 에서 bigInt 로 지정하였다. int는 4바이트 bigInt는 8바이트
	//  "BIGINT"와 Java의 "Long"이 서로 대응되는 데이터 타입
	
	// Long을 사용하는 이유는 객체로 처리하는 데 편리함을 제공하기 때문입니다.
	// 예를 들어, null 값을 처리할 수 있고, 컬렉션 등에서 사용될 수 있습니다.
	
	
	public Account toAccount(Integer userId) {
		
		Account dto = Account.builder()
							 .number(this.number)
							 .password(this.password)
							 .balance(this.balance)
							 .userId(userId)
							 .build();
		return dto;
	}

}
