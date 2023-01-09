package com.github.relucent.base.common.ldap;

/**
 * LDAP配置类
 * @author YYL
 * @version 2014-04-08
 */
public class LdapConfig {

    // ==============================Fields===========================================
    /** 连接地址(IP地址、主机名) */
    private String hostname;
    /** 连接端口号 */
    private int port = 389;
    /** AD用户名(username) */
    private String securityPrincipal;
    /** AD用户密码(password) */
    private String securityCredentials;
    /** LDAP 工厂 */
    private String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    /** 访问安全级别：none、simple、strong */
    private String securityAuthentication = "simple";

    // ==============================Methods==========================================
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    public String getSecurityCredentials() {
        return securityCredentials;
    }

    public void setSecurityCredentials(String securityCredentials) {
        this.securityCredentials = securityCredentials;
    }

    public String getInitialContextFactory() {
        return initialContextFactory;
    }

    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }

    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }
}
