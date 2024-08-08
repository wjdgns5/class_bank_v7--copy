package com.tenco.bank.repository.interfaces;

import java.util.List;

import com.tenco.bank.repository.model.History;

public interface HistoryRepository {
	
	public int insert(History history);
	
	public int updateById(History history);
	
	public int deleteById(Integer id); 
	
	// 거래내역 조회
	public History findById(Integer id);
	
	public List<History> findAll();

}
