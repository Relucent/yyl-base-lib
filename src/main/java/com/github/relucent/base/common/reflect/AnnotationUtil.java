package com.github.relucent.base.common.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

import com.github.relucent.base.common.collection.ConcurrentHashSet;

/**
 * 注解工具类
 */
public class AnnotationUtil {

    /**
     * 元注解
     */
    private static final Set<Class<? extends Annotation>> META_ANNOTATIONS;
    static {
        Set<Class<? extends Annotation>> annotations = new ConcurrentHashSet<>();
        annotations.add(Retention.class);
        annotations.add(Inherited.class);
        annotations.add(Documented.class);
        annotations.add(SuppressWarnings.class);
        annotations.add(Override.class);
        annotations.add(Deprecated.class);
        META_ANNOTATIONS = annotations;
    }

    /**
     * 是否为Jdk自带的元注解。<br>
     * 包括：
     * <ul>
     * <li>{@link Target}</li>
     * <li>{@link Retention}</li>
     * <li>{@link Inherited}</li>
     * <li>{@link Documented}</li>
     * <li>{@link SuppressWarnings}</li>
     * <li>{@link Override}</li>
     * <li>{@link Deprecated}</li>
     * </ul>
     * @param annotationType 注解类型
     * @return 是否为Jdk自带的元注解
     */
    public static boolean isJdkMetaAnnotation(Class<? extends Annotation> annotationType) {
        return META_ANNOTATIONS.contains(annotationType);
    }

    /**
     * 获取注解类的保留时间，可选值 SOURCE（源码时），CLASS（编译时），RUNTIME（运行时），默认为 CLASS
     * @param annotationType 注解类
     * @return 保留时间枚举
     */
    public static RetentionPolicy getRetentionPolicy(Class<? extends Annotation> annotationType) {
        final Retention retention = annotationType.getAnnotation(Retention.class);
        if (retention == null) {
            return RetentionPolicy.CLASS;
        }
        return retention.value();
    }

    /**
     * 获取注解类可以用来修饰哪些程序元素，如 TYPE, METHOD, CONSTRUCTOR, FIELD, PARAMETER 等
     * @param annotationType 注解类
     * @return 注解修饰的程序元素数组
     */
    public static ElementType[] getTargetType(Class<? extends Annotation> annotationType) {
        final Target target = annotationType.getAnnotation(Target.class);
        if (target == null) {
            return new ElementType[] { ElementType.TYPE, //
                    ElementType.FIELD, //
                    ElementType.METHOD, //
                    ElementType.PARAMETER, //
                    ElementType.CONSTRUCTOR, //
                    ElementType.LOCAL_VARIABLE, //
                    ElementType.ANNOTATION_TYPE, //
                    ElementType.PACKAGE//
            };
        }
        return target.value();
    }

    /**
     * 是否会保存到 Javadoc 文档中
     * @param annotationType 注解类
     * @return 是否会保存到 Javadoc 文档中
     */
    public static boolean isDocumented(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Documented.class);
    }

    /**
     * 是否可以被继承，默认为 false
     * @param annotationType 注解类
     * @return 是否会保存到 Javadoc 文档中
     */
    public static boolean isInherited(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Inherited.class);
    }

    /**
     * 获取指定注解
     * @param <A> 注解类型
     * @param element {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类型
     * @return 注解对象
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationType) {
        if (element == null) {
            return null;
        }
        Annotation[] annotations = element.getAnnotations();
        if (annotations == null) {
            return null;
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationType)) {
                return (A) annotation;
            }
        }
        return null;
    }

    /**
     * 检查是否包含指定注解指定注解
     * @param element {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类型
     * @return 是否包含指定注解
     */
    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return getAnnotation(element, annotationType) != null;
    }
}
