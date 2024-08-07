package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bank.repository.model.User;

@Mapper // // 반드시 선언해주어 한다.
public interface UserRepository {

	//interface 와 user.xml 매칭 - (메서드 명 기준)  
	
	public int insert(User user);
	public int updateById(User user);
	public int deleteById(Integer id);
	public User findById(Integer id);
	public List<User> findAll();
	
}
