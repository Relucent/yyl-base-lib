package com.github.relucent.base.plugin.security;

import java.io.Serializable;

import com.github.relucent.base.common.constants.StringConstants;

/**
 * 用户登录对象
 */
@SuppressWarnings("serial")
public class Principal implements Serializable {

    // ========================================Fields=========================================
    /** 用户ID */
    private String userId;
    /** 用户名 */
    private String username;
    /** 用户姓名 */
    private String name;
    /** 所属机构 */
    private String organizationId;
    /** 所属部门 */
    private String departmentId;
    /** 所属角色 */
    private String[] roleIds = new String[0];
    /** 拥有权限 */
    private String[] permissionIds = new String[0];

    // ========================================Constants======================================
    /** 未登录用户 */
    public static final Principal NONE;
    static {
        NONE = new Principal();
        NONE.setUserId(StringConstants.EMPTY);
        NONE.setUsername(StringConstants.EMPTY);
        NONE.setName(StringConstants.EMPTY);
        NONE.setOrganizationId(StringConstants.EMPTY);
        NONE.setDepartmentId(StringConstants.EMPTY);
    }

    // ========================================Methods========================================
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String[] roleIds) {
        this.roleIds = roleIds;
    }

    public String[] getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(String[] permissionIds) {
        this.permissionIds = permissionIds;
    }
}
