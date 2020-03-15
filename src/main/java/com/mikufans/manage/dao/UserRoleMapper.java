package com.mikufans.manage.dao;

import com.mikufans.manage.pojo.UserRoleKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper
{
    int deleteByPrimaryKey(UserRoleKey key);

    int insert(UserRoleKey record);

    int insertSelective(UserRoleKey record);

    List<UserRoleKey> findByUserId(int userId);
}
