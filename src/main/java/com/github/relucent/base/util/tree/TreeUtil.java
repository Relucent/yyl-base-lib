package com.github.relucent.base.util.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Tree类型工具类
 * @author YYL
 */
public class TreeUtil {

    private static final String PATH_SEPARATOR = "/";

    /**
     * 构建树模型
     * @param <T> 原始数据类型泛型
     * @param <N> 树节点对象泛型
     * @param <I> ID类型泛型
     * @param parentId 父节点ID
     * @param data 数据
     * @param adapter 节点适配器
     * @param idGetter 节点ID访问器
     * @param parentIdGetter 节点父ID访问器
     * @param childrenSetter 子节点设置器
     * @return 树模型
     */
    @SuppressWarnings("unchecked")
    public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter, IdGetter<T, I> idGetter,
            ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter) {
        return buildTree(parentId, data, adapter, DEFAULT_NODE_FILTER, idGetter, parentIdGetter, childrenSetter, (Comparator<N>) NONE_COMPARATOR, 0);
    }

    /**
     * 构建树模型
     * @param <T> 原始数据类型泛型
     * @param <N> 树节点对象泛型
     * @param <I> ID类型泛型
     * @param parentId 父节点ID
     * @param data 数据
     * @param adapter 节点适配器
     * @param idGetter 节点ID访问器
     * @param parentIdGetter 节点父ID访问器
     * @param childrenSetter 子节点设置器
     * @param comparator 排序比较器
     * @return 树模型
     */
    @SuppressWarnings("unchecked")
    public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter, IdGetter<T, I> idGetter,
            ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter, Comparator<N> comparator) {
        return buildTree(parentId, data, adapter, DEFAULT_NODE_FILTER, idGetter, parentIdGetter, childrenSetter, (Comparator<N>) NONE_COMPARATOR, 0);
    }

    /**
     * 构建树模型
     * @param <T> 原始数据类型泛型
     * @param <N> 树节点对象泛型
     * @param <I> ID类型泛型
     * @param parentId 父节点ID
     * @param data 数据
     * @param adapter 节点适配器
     * @param filter 节点过滤器
     * @param idGetter 节点ID访问器
     * @param parentIdGetter 节点父ID访问器
     * @param childrenSetter 子节点设置器
     * @return 树模型
     */
    @SuppressWarnings("unchecked")
    public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter, NodeFilter<T> filter, IdGetter<T, I> idGetter,
            ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter) {
        return buildTree(parentId, data, adapter, filter, idGetter, parentIdGetter, childrenSetter, (Comparator<N>) NONE_COMPARATOR, 0);
    }

    /**
     * 构建树模型
     * @param <T> 原始数据类型泛型
     * @param <N> 树节点类型泛型
     * @param <I> ID类型泛型
     * @param parentId 父节点ID
     * @param data 数据
     * @param adapter 节点适配器
     * @param filter 节点过滤器
     * @param idGetter 节点ID访问器
     * @param parentIdGetter 节点父ID访问器
     * @param childrenSetter 子节点设置器
     * @param comparator 排序比较器
     * @return 树模型
     */
    public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter, NodeFilter<T> filter, IdGetter<T, I> idGetter,
            ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter, Comparator<N> comparator) {
        return buildTree(parentId, data, adapter, filter, idGetter, parentIdGetter, childrenSetter, comparator, 0);
    }

    /**
     * 构建树模型
     * @param <T> 原始数据类型泛型
     * @param <N> 树节点类型泛型
     * @param <I> ID类型泛型
     * @param parentId 父节点ID
     * @param data 数据
     * @param adapter 节点适配器
     * @param filter 节点过滤器
     * @param idGetter 节点ID访问器
     * @param parentIdGetter 节点父ID访问器
     * @param childrenSetter 子节点设置器
     * @param comparator 排序比较器
     * @param depth 节点层级
     * @return 树模型
     */
    private static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter, NodeFilter<T> filter, IdGetter<T, I> idGetter,
            ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter, Comparator<N> comparator, int depth) {
        List<N> nodes = new ArrayList<>();
        for (T model : data) {
            if (Objects.equals(parentId, parentIdGetter.getParentId(model))) {
                List<N> children =
                        buildTree(idGetter.getId(model), data, adapter, filter, idGetter, parentIdGetter, childrenSetter, comparator, depth + 1);
                if (filter.accept(model, depth, children.isEmpty())) {
                    N node = adapter.adapte(model);
                    childrenSetter.setChildren(node, children);
                    nodes.add(node);
                }
            }
        }
        if (comparator != NONE_COMPARATOR) {
            Collections.sort(nodes, comparator);
        }
        return nodes;
    }

    /**
     * 递归设置 ID_PATH
     * @param <N> 节点对象泛型
     * @param <I> ID类型泛型
     * @param nodes 节点列表
     * @param idGetter 节点ID访问器
     * @param parentIdGetter 节点父ID访问器
     * @param idPathSetter ID路径设置器
     * @param parentId 上级ID
     * @param parentIdPath 上级ID路径
     */
    public static <N, I> void recursiveSetIdPath(Collection<N> nodes, IdGetter<N, I> idGetter, ParentIdGetter<N, I> parentIdGetter,
            IdPathSetter<N> idPathSetter, I parentId, String parentIdPath) {
        for (N node : nodes) {
            if (Objects.equals(parentId, parentIdGetter.getParentId(node))) {
                I id = idGetter.getId(node);
                String idPath = parentIdPath + id + PATH_SEPARATOR;
                idPathSetter.setIdPath(node, idPath);
                recursiveSetIdPath(nodes, idGetter, parentIdGetter, idPathSetter, id, idPath);
            }
        }
    }

    /**
     * 默认的节点过滤器
     * @return 默认的节点过滤器
     * @param <T> 数据类型泛型
     */
    @SuppressWarnings("unchecked")
    public static <T> NodeFilter<T> defaultFilter() {
        return DEFAULT_NODE_FILTER;
    }


    /**
     * 数据ID访问器
     * @param <T> 数据对象泛型
     * @param <I> ID类型泛型
     */
    @FunctionalInterface
    public static interface IdGetter<T, I> {
        /**
         * 获得ID
         * @param model 模型对象
         * @return ID
         */
        I getId(T model);
    }

    /**
     * 父ID访问器
     * @param <T> 数据对象泛型
     * @param <I> ID类型泛型
     */
    @FunctionalInterface
    public static interface ParentIdGetter<T, I> {
        /**
         * 获得父节点ID
         * @param model 模型对象
         * @return 上级ID
         */
        I getParentId(T model);
    }

    /**
     * 子节点设置器
     * @param <N> 节点对象泛型
     */
    @FunctionalInterface
    public static interface ChildrenSetter<N> {
        /**
         * 设置子节点
         * @param node 节点对象
         * @param children 子节点
         */
        void setChildren(N node, List<N> children);
    }

    /**
     * 节点ID设置器
     * @param <N> 节点对象泛型
     */
    @FunctionalInterface
    public static interface IdPathSetter<N> {
        /**
         * ID路径
         * @param node 节点对象
         * @param idPath ID路径
         */
        void setIdPath(N node, String idPath);
    }

    /**
     * 节点过滤器
     * @param <T> 数据类型泛型
     */
    @FunctionalInterface
    public static interface NodeFilter<T> {
        /**
         * 判断树中是否应含有该节点
         * @param model 节点代表的的对象
         * @param depth 节点在树中所在的层次
         * @param leaf 是否叶子节点
         * @return 如果树中不包含该节点，则返回true，否则返回false。
         */
        boolean accept(T model, int depth, boolean leaf);

        /**
         * 创建联合条件过滤器
         * @param other 节点过滤器
         * @return 联合条件节点过滤器
         */
        default NodeFilter<T> and(NodeFilter<? super T> other) {
            Objects.requireNonNull(other);
            return (p1, p2, p3) -> accept(p1, p2, p3) && other.accept(p1, p2, p3);
        }
    }

    /**
     * 节点适配器
     * @param <T> 模型对象类型泛型
     * @param <N> 节点对象类型泛型
     */
    @FunctionalInterface
    public static interface NodeAdapter<T, N> {
        /**
         * 将数据模型转换为树节点模型
         * @param model 数据模型
         * @return 树节点模型
         */
        N adapte(T model);
    }

    /** 默认过滤器(不做筛选) */
    @SuppressWarnings("rawtypes")
    public static final NodeFilter DEFAULT_NODE_FILTER = new NodeFilter() {
        @Override
        public boolean accept(Object model, int depth, boolean leaf) {
            return true;
        }
    };

    /** 默认排序比较器(代表不排序) */
    private static final Comparator<?> NONE_COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return 0;
        }
    };
}
