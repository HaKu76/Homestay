package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.dto.query.extend.UserQueryDto;
import com.example.pojo.dto.update.UserLoginDTO;
import com.example.pojo.dto.update.UserRegisterDTO;
import com.example.pojo.dto.update.UserUpdateDTO;
import com.example.pojo.entity.User;
import com.example.pojo.vo.ChartVO;
import com.example.pojo.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务类
 */
public interface UserService {
    Result<String> register(UserRegisterDTO userRegisterDTO);

    Result<Object> login(UserLoginDTO userLoginDTO);

    Result<UserVO> auth();

    Result<List<User>> query(UserQueryDto userQueryDto);

    Result<String> update(UserUpdateDTO userUpdateDTO);

    Result<String> batchDelete(List<Integer> ids);

    Result<String> updatePwd(Map<String, String> map);

    Result<UserVO> getById(Integer id);

    Result<String> insert(UserRegisterDTO userRegisterDTO);

    Result<String> backUpdate(User user);

    Result<List<ChartVO>> daysQuery(Integer day);

}
