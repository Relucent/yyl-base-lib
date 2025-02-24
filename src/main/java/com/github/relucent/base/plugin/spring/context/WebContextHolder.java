package com.github.relucent.base.plugin.spring.context;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 上下文信息持有者
 * @author YYL
 */
public class WebContextHolder {

	/**
	 * 获得HTTP会话
	 * @return HTTP会话
	 */
	public static HttpSession getSession() {
		HttpServletRequest request = getRequest();
		return request != null ? request.getSession() : null;
	}

	/**
	 * 获得HTTP请求
	 * @return HTTP请求
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes attributes = getRequestAttributes();
		return attributes != null ? attributes.getRequest() : null;
	}

	/**
	 * 设置Session属性值
	 * @param <T>   属性泛型
	 * @param name  属性名
	 * @param value 属性值
	 */
	public static <T> void setSessionAttribute(String name, T value) {
		HttpSession session = getSession();
		if (session != null) {
			session.setAttribute(name, (Serializable) value);
		}
	}

	/**
	 * 移除Session属性值
	 * @param name 属性名
	 */
	public static void removeSessionAttribute(String name) {
		HttpSession session = getSession();
		if (session != null) {
			session.removeAttribute(name);
		}
	}

	/**
	 * 销毁Session
	 */
	public static void invalidateSession() {
		HttpSession session = getSession();
		if (session != null) {
			session.invalidate();
		}
	}

	/**
	 * 获得Session属性值
	 * @param <T>  属性泛型
	 * @param name 属性名
	 * @return 属性值
	 */
	public static <T> T getSessionAttribute(String name) {
		return getSessionAttribute(getSession(), name);
	}

	/**
	 * 获得Session属性值
	 * @param <T>     属性泛型
	 * @param session HTTP会话
	 * @param name    属性名
	 * @return 属性值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(HttpSession session, String name) {
		return session != null ? (T) session.getAttribute(name) : null;
	}

	/**
	 * 获得 ServletRequestAttributes
	 * @return ServletRequestAttributes
	 */
	private static ServletRequestAttributes getRequestAttributes() {
		return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	}
}
