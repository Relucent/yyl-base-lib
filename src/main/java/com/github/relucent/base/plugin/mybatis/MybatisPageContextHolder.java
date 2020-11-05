package com.github.relucent.base.plugin.mybatis;

/**
 * 分页上下文持有者
 */
public class MybatisPageContextHolder {

    private static final ThreadLocal<MybatisPageContext> CONTEXT_HOLDER = ThreadLocal.withInitial(MybatisPageContext::new);

    /**
     * 获取当前的分页上下文
     * @return 分页上下文
     */
    public static MybatisPageContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 设置分页上下文
     * @param context 分页上下文
     */
    public static void setContext(MybatisPageContext context) {
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 从当前线程显式清除上下文值
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}
