package com.tenco.bank.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ValueFormatter {
	
	public String timestampToString(Timestamp timestamp) {
		// Timestamp : 날짜와 시간을 나타내는 Java의 클래스
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
		// SimpleDateFormat : 날짜와 시간을 특정 형식으로 변환하기 위해 사용
		return sdf.format(timestamp);
		//format 메서드를 사용하여 Timestamp를 문자열로 변환
	}
	
	public String formatKoreanWon(Long amount) {
		// DecimalFormat :  숫자를 형식화 ( 숫자를 특정 형식으로 변환)
		DecimalFormat df = new DecimalFormat("#,###"); // #은 숫자가 있으면 표시, ','는 천 단위 구분 기호를 추가
		String formatNumber = df.format(amount);
		return formatNumber +"원";
	}

}
