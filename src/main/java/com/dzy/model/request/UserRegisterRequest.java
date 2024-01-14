package com.dzy.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @auther DZY
 * @date 2023/5/31 - 11:49
 */
@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7302758719608090531L;

    //用户账号
    private String userAccount;

    //用户密码
    private String userPassword;

    //用户确认密码
    private String checkPassword;
}
