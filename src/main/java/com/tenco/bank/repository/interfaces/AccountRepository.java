package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.Account;

@Mapper 
public interface AccountRepository {
// 인터페이스와 account.xml 파일을 매칭 시킨다.
	
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id, String name);
	
	
	// @Param :  Mybatis의 SQL 문장에 다수의 파라미터를 전달할 때
	// --> 한사람에 유저는 여러개의 계좌 번호를 가질 수 있다. 
	public List<Account> findByUserId(@Param("userId") Integer principalId);
	
	public Account findByNumber(@Param("number") String id);
	
	
	
}
