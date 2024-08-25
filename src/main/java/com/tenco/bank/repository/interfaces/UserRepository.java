package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.User;

@Mapper // // 반드시 선언해주어 한다.
public interface UserRepository {

	//interface 와 user.xml 매칭 - (메서드 명 기준)  
	
	public int insert(User user); // 유저를 등록하는 메서드
	public int updateById(User user); // 유저 아이디로 확인하여 유저 개인정보 변경함
	public int deleteById(Integer id); // 유저 아이디로 확인하여 유저 데이터를 삭제함
	public User findById(Integer id); // 유저 아이디로 유저 데이터를 조회한다.
	public List<User> findAll(); // 모든 유저의 정보를 조회한다.
	
	public User findByUsernameAndPassword(@Param("username") String username, @Param("password")String password);
	
	// 코드 추가
	public User findByUsername(@Param("username") String username);
}
