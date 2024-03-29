package com.dzy.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzy.constant.StatusCode;
import com.dzy.exception.BusinessException;
import com.dzy.model.domain.User;
import com.dzy.service.UserService;
import com.dzy.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dzy.constant.UserConstant.ADMIN_ROLE;
import static com.dzy.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author DZY
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-05-24 21:15:49
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    //盐值
    private static final String SALT = "0JV2Nw26";

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册实现
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 用户确认密码
     * @return 数值
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //校验
        //账号，密码，确认密码是否为空，空字符串，含有空格
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(StatusCode.PARAM_NULL_ERROR, "账号、密码或确认密码为空或含有空字符串、空格");
        }
        //账号长度是否合法
        if (userAccount.length() < 4) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "账号长度小于6个字符");
        }
        //密码长度是否合法
        if (userPassword.length() < 6 || userPassword.length() > 20) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "密码长度小于6个字符，超过20个字符");
        }
        //确认密码长度是否合法
        if (checkPassword.length() < 6 || checkPassword.length() > 20) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "确认密码长度小于6个字符，超过20个字符");
        }
        //账号是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "账号已被注册");
        }
        //账号是否合法
        //只能包括中文英文字母和下划线
        String validPattern = "[\\u4E00-\\u9FA5A-Za-z0-9_]{4,}";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "账号存在非法字符");
        }
        //密码是否合法
        //只能包括英文数字下划线和常用特殊字符
        if (!userPassword.matches("[A-Za-z0-9_~!@#$%^&*()+]{6,20}")) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "密码存在非法字符");
        }
        //密码和确认密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "密码和确认密码不同");
        }
        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //用户数据加入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        int insertResult = userMapper.insert(user);
        if (insertResult != 1) {
            throw new BusinessException(StatusCode.DATABASE_ERROR, "用户注册数据没有加入数据库");
        }
        // TODO: 2024/1/13 返回的可以是用用户id,且可以将查询用户id方法封装
        return 1;
    }

    /**
     * 用户登录实现
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 数值
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //账户是否非空，空字符串，含有空格
        if (StringUtils.isBlank(userAccount)) {
            throw new BusinessException(StatusCode.PARAM_NULL_ERROR, "账户为空或含有空字符串、空格");
        }
        //密码是否非空，空字符串，含有空格
        if (StringUtils.isBlank(userPassword)) {
            throw new BusinessException(StatusCode.PARAM_NULL_ERROR, "密码为空或含有空字符串、空格");
        }
        //账号长度是否合法
        if (userAccount.length() < 4) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "账号长度小于6个字符");
        }
        //密码长度是否合法
        if (userPassword.length() < 6 || userPassword.length() > 20) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "密码长度小于6个字符，超过20个字符");
        }
        //账号是否合法
        String validPattern = "[\\u4E00-\\u9FA5A-Za-z0-9_]{4,}";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "账号存在非法字符");
        }
        //密码是否合法
        if (!userPassword.matches("[A-Za-z0-9_~!@#$%^&*()+]{6,20}")) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "密码存在非法字符");
        }
        //账号和密码是否正确
        //查询没有被逻辑删除的数据，mybatis-plus配置逻辑删除！！！
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User loginUser = userMapper.selectOne(queryWrapper);
        if (loginUser == null) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "账号不存在或密码错误");
        }
        //用户脱敏
        User safetyUser = getSafetyUser(loginUser);
        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 按照用户昵称查询用户
     *
     * @param userName 用户名
     * @return 用户列表
     */
    @Override
    public List<User> userSearchBatches(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            //模糊查询%xxx%
            queryWrapper.like("userName", userName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        //用户脱敏
        //遍历数据库查询的数据，对每条数据进行脱敏
        //可以使用流处理，stream流！！！
        List<User> safetyUserList = new ArrayList<>();
        for (User user : userList) {
            safetyUserList.add(getSafetyUser(user));
        }
        return safetyUserList;
    }

    /**
     * 用户脱敏
     *
     * @param loginUser 数据库User对象
     * @return 脱敏后User
     */
    @Override
    public User getSafetyUser(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(StatusCode.DATA_NULL_ERROR);
        }
        User safetyUser = new User();
        safetyUser.setUserId(loginUser.getUserId());
        safetyUser.setUserName(loginUser.getUserName());
        safetyUser.setUserAccount(loginUser.getUserAccount());
        safetyUser.setGender(loginUser.getGender());
        safetyUser.setBirthday(loginUser.getBirthday());
        safetyUser.setPhone(loginUser.getPhone());
        safetyUser.setEmail(loginUser.getEmail());
        safetyUser.setAvatarUrl(loginUser.getAvatarUrl());
        safetyUser.setUserRole(loginUser.getUserRole());
        safetyUser.setUserStatus(loginUser.getUserStatus());
        safetyUser.setCreateTime(loginUser.getCreateTime());
        safetyUser.setUpdateTime(loginUser.getUpdateTime());
        return safetyUser;
    }

    /**
     * 用户是否为管理员
     *
     * @param request 请求域
     * @return boolean
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 用户退出登录
     *
     * @param request 请求域
     * @return 成功退出返回1，否则返回-1
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        return attribute == null ? 1 : -1;
    }
}




