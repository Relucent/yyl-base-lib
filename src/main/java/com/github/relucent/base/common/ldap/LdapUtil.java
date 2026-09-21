package com.github.relucent.base.common.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * LDAP的访问工具类
 * @author YYL
 * @version 2012-10-13
 */
public class LdapUtil {

    // ==============================Constructors=====================================
    /**
     * 构造函数
     */
    protected LdapUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 获取LDAP连接上下文
     * @param ldapConfig LDAP连接配置
     * @return LDAP连接上下文
     * @throws NamingException 访问LDAP异常
     */
    public static LdapContext getLdapContext(LdapConfig ldapConfig) throws NamingException {
        Hashtable<String, String> environment = newEnvironmentMap();
        String scheme = ldapConfig.isSecure() ? "ldaps" : "ldap";
        environment.put(Context.PROVIDER_URL, scheme + "://" + ldapConfig.getHostname() + ":" + ldapConfig.getPort());
        environment.put(Context.INITIAL_CONTEXT_FACTORY, ldapConfig.getInitialContextFactory());
        environment.put(Context.SECURITY_AUTHENTICATION, ldapConfig.getSecurityAuthentication());
        environment.put(Context.SECURITY_PRINCIPAL, ldapConfig.getSecurityPrincipal());
        environment.put(Context.SECURITY_CREDENTIALS, ldapConfig.getSecurityCredentials());
        return new InitialLdapContext(environment, null);
    }

    /**
     * 关闭LDAP连接
     * @param ctx 连接上下文
     */
    public static void closeQuietly(LdapContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * 静默关闭LDAP查询结果枚举，避免底层连接/上下文资源泄漏
     * @param enumeration 查询结果枚举
     */
    public static void closeQuietly(NamingEnumeration<?> enumeration) {
        if (enumeration != null) {
            try {
                enumeration.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * 创建新条目
     * @param entry 新条目
     * @param ctx   LDAP上下文连接
     * @throws NamingException 访问LDAP异常
     */
    public static void create(LdapEntry entry, LdapContext ctx) throws NamingException {
        String dn = entry.getDn();
        Attributes attributes = new BasicAttributes(true);
        if (entry != null && !entry.isEmpty()) {
            Iterator<String> iterator = entry.keySet().iterator();
            while (iterator.hasNext()) {
                String id = iterator.next();
                Attribute attr = new BasicAttribute(id);
                List<?> values = entry.getAll(id);
                if (values != null) {
                    for (Object value : values) {
                        attr.add(value);
                    }
                }
                attributes.put(attr);
            }
        }
        ctx.createSubcontext(dn, attributes);
    }

    /**
     * 删除条目
     * @param dn  条目的完整DN
     * @param ctx LDAP上下文连接
     * @throws NamingException 访问LDAP异常
     */
    public static void delete(String dn, LdapContext ctx) throws NamingException {
        if (dn == null || dn.isEmpty()) {
            throw new IllegalArgumentException("dn must not be null or empty");
        }
        if (ctx == null) {
            throw new IllegalArgumentException("ctx must not be null");
        }
        ctx.destroySubcontext(dn);
    }

    /**
     * 修改条目
     * @param entry 修改的条目
     * @param ctx   LDAP上下文连接
     * @throws NamingException 访问LDAP异常
     */
    public static void modify(LdapEntry entry, LdapContext ctx) throws NamingException {
        String dn = entry.getDn();
        Attributes attributes = new BasicAttributes(true);
        if (entry != null && !entry.isEmpty()) {
            Iterator<String> iterator = entry.keySet().iterator();
            while (iterator.hasNext()) {
                String id = iterator.next();
                Attribute attr = new BasicAttribute(id.toString());
                List<?> values = entry.getAll(id);
                if (values != null) {
                    for (Object value : values) {
                        attr.add(value);
                    }
                }
                attributes.put(attr);
            }
        }
        ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attributes);
    }

    /**
     * 查找条目
     * @param base          基准，意为从某个条目下搜索
     * @param filter        过滤条件
     * @param searchAttrSet 查找的字段
     * @param ctx           LDAP上下文连接
     * @return 条目列表
     * @throws NamingException 访问LDAP异常
     */
    public static List<LdapEntry> find(String base, String filter, Set<String> searchAttrSet, LdapContext ctx)
            throws NamingException {
        SearchControls searchcontrols = new SearchControls();
        searchcontrols.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // searchAttrSet 为 null 时返回全部属性，避免 NPE
        searchcontrols.setReturningAttributes(
                searchAttrSet == null ? null : searchAttrSet.toArray(new String[searchAttrSet.size()]));
        NamingEnumeration<SearchResult> namingenumeration = null;
        try {
            namingenumeration = ctx.search(base, filter, searchcontrols);
            List<LdapEntry> entries = new ArrayList<LdapEntry>();
            // 得到所有符合条件的条目
            if (namingenumeration != null) {
                while (namingenumeration.hasMore()) {
                    SearchResult searchresult = namingenumeration.next();
                    String dn = base.length() > 0 ? searchresult.getName() + "," + base : searchresult.getName();
                    entries.add(toLdapEntry(searchresult.getAttributes(), dn));
                }
            }
            return entries;
        } finally {
            closeQuietly(namingenumeration);
        }
    }

    /**
     * 查找条目
     * @param base          基准，意为从某个条目下搜索
     * @param filter        过滤条件
     * @param searchAttrSet 查找的字段
     * @param ctx           LDAP上下文连接
     * @return 条目列表
     * @throws NamingException 访问LDAP异常
     */
    public static LdapEntry get(String base, String filter, Set<String> searchAttrSet, LdapContext ctx)
            throws NamingException {
        SearchControls searchcontrols = new SearchControls();
        searchcontrols.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // searchAttrSet 为 null 时返回全部属性，避免 NPE
        searchcontrols.setReturningAttributes(
                searchAttrSet == null ? null : searchAttrSet.toArray(new String[searchAttrSet.size()]));
        NamingEnumeration<SearchResult> namingenumeration = null;
        try {
            namingenumeration = ctx.search(base, filter, searchcontrols);
            // 得到所有符合条件的条目
            if (namingenumeration != null) {
                while (namingenumeration.hasMore()) {
                    SearchResult searchresult = namingenumeration.next();
                    String dn = base.length() > 0 ? searchresult.getName() + "," + base : searchresult.getName();
                    return toLdapEntry(searchresult.getAttributes(), dn);
                }
            }
            return null;
        } finally {
            closeQuietly(namingenumeration);
        }
    }

    /**
     * 创建作为环境参数用的 Hashtable 对象
     * @return Hashtable 对象
     */
    private static Hashtable<String, String> newEnvironmentMap() {
        return new Hashtable<String, String>();
    }

    /**
     * 将Attributes转换为 LdapEntry对象
     * @param attributes LDAP属性对象
     * @param dn         DN
     * @return 条目信息对象
     * @throws NamingException 访问LDAP异常
     */
    private static LdapEntry toLdapEntry(Attributes attributes, String dn) throws NamingException {
        LdapEntry entry = new LdapEntry(dn);
        NamingEnumeration<String> ids = attributes.getIDs();
        try {
            while (ids.hasMore()) {
                String id = (String) ids.next();
                Attribute attribute = attributes.get(id);
                List<Object> values = new ArrayList<Object>();
                NamingEnumeration<?> en = attribute.getAll();
                try {
                    while (en.hasMore()) {
                        // 保留原始类型(byte[]/String/Integer 等)，不强制 toString，
                        // 避免类型丢失，且二进制属性(byte[])不会被误当作 UTF-8 文本而损坏
                        values.add(en.next());
                    }
                } finally {
                    closeQuietly(en);
                }
                entry.putAll(id, values);
            }
        } finally {
            closeQuietly(ids);
        }
        return entry;
    }
}
