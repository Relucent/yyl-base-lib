package com.github.relucent.base.plug.security;

import java.io.Serializable;

/**
 * 认证令牌
 */
@SuppressWarnings("serial")
public class AuthToken implements Serializable {

    // ========================================Fields=========================================
    /** 账号 */
    private String account;
    /** 密码 */
    private String password;

    // ========================================Methods========================================
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
