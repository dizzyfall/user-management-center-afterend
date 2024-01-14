package com.dzy.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @auther DZY
 * @date 2023/5/31 - 15:26
 */
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8853029901957331675L;

    //用户账号
    private String userAccount;

    //用户密码
    private String userPassword;
}
