package com.dzy.service;

import com.dzy.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author DZY
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-05-24 21:15:49
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户第一次密码
     * @param checkPassword 用户第二次密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 用户对象
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 按照用户昵称查询用户
     *
     * @param userName 用户名
     * @return 用户列表
     */
    List<User> userSearchBatches(String userName);

    /**
     * 用户脱敏
     *
     * @param loginUser 数据库User对象
     * @return 脱敏后User
     */
    User getSafetyUser(User loginUser);

    /**
     * 是否为管理员
     *
     * @param request 请求域
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户退出登录
     *
     * @param request
     * @return 成功退出返回1，否则返回-1
     */
    int userLogout(HttpServletRequest request);

}
