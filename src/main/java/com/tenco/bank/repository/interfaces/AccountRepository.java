package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.HistoryAccount;

@Mapper 
public interface AccountRepository {
// 인터페이스와 account.xml 파일을 매칭 시킨다.
	
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id, String name);
	
	
	// @Param :  Mybatis의 SQL 문장에 다수의 파라미터를 전달할 때
	// --> 한사람에 유저는 여러개의 계좌 번호를 가질 수 있다. 
	public List<Account> findByUserId(@Param("userId") Integer principalId); // 유저 아이디로 찾는다.
	// ( @Param("여기는DTO의 문자를 넣는다.") 여기는 들어갈 값 )
	
	//  파라미터가 두개일경우 마이바티스에서 인식을 못하기때문에 @param을 사용해야한다.
	// 코드 추가 예정
		public Account findByNumber(@Param("number") String id); 
		public Account findByAccountId(Integer accountId);
		
		//코드 추가 예정 - 모델을 반드시  1:1 엔터티에 매핑을 시킬 필요는 없다. 
		// 조인 쿼리, 서브쿼리, 동적쿼리 , type=all, de.., accountId
		public List<HistoryAccount> findByAccountIdAndTypeOfHistory(@Param("type") String type, 
																	    @Param("accountId") Integer accountId,
																	    @Param("limit") int limit,
																	    @Param("offset") int offset);
			
		public int countByAccountIdAndType(@Param("type")String type, @Param("accountId")Integer accountId);

}
