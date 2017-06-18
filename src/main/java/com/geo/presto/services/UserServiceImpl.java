package com.geo.presto.services;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.geo.presto.bean.User;
import com.geo.presto.bean.UserExample;
import com.geo.presto.dao.UserMapper;

@Repository
public class UserServiceImpl implements UserService {

	 
	
	@Autowired
    private UserMapper userDao; 
	
	@Override
	public int countByExample(UserExample example) {

		return userDao.countByExample(example);
	}

	@Override
	public int deleteByExample(UserExample example) {
	
		return userDao.deleteByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(String username) {
		 
		return userDao.deleteByPrimaryKey(username);
	}

	@Override
	public int insert(User record) {
	
		return userDao.insert(record);
	}

	@Override
	public int insertSelective(User record) {
		
		return userDao.insertSelective(record);
	}

	@Override
	public List<User> selectByExample(UserExample example) {
		 
		return userDao.selectByExample(example);
	}

	@Override
	public User selectByPrimaryKey(String username) {
		long s=System.currentTimeMillis();
		User i = userDao.selectByPrimaryKey(username);
		return i;
	}

	@Override
	public int updateByExampleSelective(User record, UserExample example) {
		
		return userDao.updateByExampleSelective(record, example);
	}

	@Override
	public int updateByExample(User record, UserExample example) {
		 
		return userDao.updateByExample(record, example);
	}

	@Override
	public int updateByPrimaryKeySelective(User record) {
		
		return userDao.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(User record) {
		
		return userDao.updateByPrimaryKey(record);
	}
}
