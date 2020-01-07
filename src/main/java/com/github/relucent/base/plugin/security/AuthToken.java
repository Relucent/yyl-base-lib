package com.github.relucent.base.plugin.security;

import java.io.Serializable;

/**
 * 认证令牌
 */
@SuppressWarnings("serial")
public class AuthToken implements Serializable {

    // ========================================Fields=========================================
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;

    // ========================================Methods========================================
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
