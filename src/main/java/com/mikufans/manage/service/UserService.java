package com.mikufans.manage.service;

import com.mikufans.manage.entity.UserDTO;
import com.mikufans.manage.entity.UserRoleVo;
import com.mikufans.manage.entity.UserSearchDTO;
import com.mikufans.manage.pojo.User;
import com.mikufans.manage.util.PageDataResult;
import org.omg.CORBA.INTERNAL;

public interface UserService
{
    /**
     * 分页查询用户列表
     * @param userSearch
     * @param page
     * @param limit
     * @return
     */
    PageDataResult getUser(UserSearchDTO userSearch, int page, int limit);

    String setUser(User user,String roleIds);

    String setJobUser(Integer id, Integer isJob, Integer insertUid,Integer version);

    String setDelUser(Integer id,Integer isDel,Integer intsetUid,Integer version);

    UserRoleVo getUserAndRoles(Integer id);

    String sendMsg(UserDTO user);

    User findUserByMobile(String mobile);

    String sendMessage(int userId,String mobile);

    int updatePwd(Integer id,String password);

    int setUserLockNum(Integer id,int isLock);

}
