package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.HistoryAccount;

@Mapper
public interface HistoryRepository {
	
	public int insert(History history);
	
	public int updateById(History history);
	
	public int deleteById(Integer id); 
	
	// 거래내역 조회
	public History findById(Integer id);
	
	public List<History> findAll();
	
	// 조인 쿼리, 서브쿼리, 동적쿼리 , type=all, de.., accountId
	public List<HistoryAccount> findByAccountIdAndTypeOfHistory(@Param("type") String type,
																@Param("accountId") Integer accountId);
	
	public int countByAccountIdAndType(@Param("type") String type, @Param("accountId") Integer accountId);
	// 조인 쿼리, 서브쿼리, 동적쿼리 , type=all, de.., accountId
		
}

