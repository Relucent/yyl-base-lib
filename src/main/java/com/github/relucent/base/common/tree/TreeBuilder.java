package com.github.relucent.base.common.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Tree 构建工具类
 * @param <T> 原始数据类型（例如数据库表实体）
 * @param <N> 树节点类型（构建后的节点对象）
 * @param <I> ID 类型（节点唯一标识，例如 Integer, Long, String）
 * @author YYL
 */
public class TreeBuilder<T, N, I> {

    // =================================Constants=============================================
    /**
     * 循环检测策略
     */
    public enum CyclePolicy {
        /** 直接跳过循环节点（默认） */
        SKIP,
        /** 检测到循环即抛异常 */
        ERROR;
    }

    // =================================Fields================================================
    private Function<T, I> idGetter;
    private Function<T, I> parentIdGetter;
    private Function<T, N> adapter;
    private BiConsumer<N, List<N>> childrenSetter;

    private Comparator<N> comparator = null;
    private NodeFilter<T> filter = (model, depth, isLeaf) -> true;
    private CyclePolicy cyclePolicy = CyclePolicy.SKIP;

    private List<T> data;
    private I rootParentId;

    // 用于循环检测
    private final Set<I> visiting = ConcurrentHashMap.newKeySet();
    private boolean visitingNull = false;

    // =================================Constructors===========================================
    private TreeBuilder() {
    }

    public static <T, N, I> TreeBuilder<T, N, I> builder() {
        return new TreeBuilder<>();
    }

    // =================================Methods================================================
    public TreeBuilder<T, N, I> data(List<T> data) {
        this.data = data;
        return this;
    }

    public TreeBuilder<T, N, I> rootParentId(I parentId) {
        this.rootParentId = parentId;
        return this;
    }

    public TreeBuilder<T, N, I> idGetter(Function<T, I> getter) {
        this.idGetter = getter;
        return this;
    }

    public TreeBuilder<T, N, I> parentIdGetter(Function<T, I> getter) {
        this.parentIdGetter = getter;
        return this;
    }

    public TreeBuilder<T, N, I> nodeAdapter(Function<T, N> adapter) {
        this.adapter = adapter;
        return this;
    }

    public TreeBuilder<T, N, I> childrenSetter(BiConsumer<N, List<N>> setter) {
        this.childrenSetter = setter;
        return this;
    }

    public TreeBuilder<T, N, I> comparator(Comparator<N> comparator) {
        this.comparator = comparator;
        return this;
    }

    public TreeBuilder<T, N, I> nodeFilter(NodeFilter<T> filter) {
        this.filter = filter;
        return this;
    }

    public TreeBuilder<T, N, I> cyclePolicy(CyclePolicy policy) {
        this.cyclePolicy = policy;
        return this;
    }

    /**
     * 构建树
     */
    public List<N> build() {
        Objects.requireNonNull(data, "data cannot be null");
        Objects.requireNonNull(idGetter, "idGetter cannot be null");
        Objects.requireNonNull(parentIdGetter, "parentIdGetter cannot be null");
        Objects.requireNonNull(adapter, "adapter cannot be null");
        Objects.requireNonNull(childrenSetter, "childrenSetter cannot be null");

        Map<I, List<T>> parentIndex = indexByParent();

        return buildSubtree(rootParentId, parentIndex, 0);
    }

    /**
     * 构建 parentId -> 子节点列表 的索引（O(n)）
     */
    private Map<I, List<T>> indexByParent() {
        Map<I, List<T>> map = new HashMap<>();
        for (T model : data) {
            I pid = parentIdGetter.apply(model);
            map.computeIfAbsent(pid, k -> new ArrayList<>()).add(model);
        }
        return map;
    }

    /**
     * 构建子树（递归 + 循环检测）
     */
    private List<N> buildSubtree(I parentId, Map<I, List<T>> index, int depth) {

        // ----- 循环检测 -----
        if (parentId == null) {
            if (visitingNull) {
                switch (cyclePolicy) {
                case ERROR:
                    throw new IllegalStateException("Cycle detected at null rootParentId");
                case SKIP:
                    return Collections.emptyList();
                }
            }
            visitingNull = true;
        } else {
            if (!visiting.add(parentId)) {
                switch (cyclePolicy) {
                case ERROR:
                    throw new IllegalStateException("Cycle detected at parentId=" + parentId);
                case SKIP:
                    return Collections.emptyList();
                }
            }
        }

        List<T> childrenData = index.getOrDefault(parentId, Collections.emptyList());
        List<N> result = new ArrayList<>(childrenData.size());

        for (T model : childrenData) {
            I id = idGetter.apply(model);

            List<N> childNodes = buildSubtree(id, index, depth + 1);
            boolean isLeaf = childNodes.isEmpty();

            if (filter.accept(model, depth, isLeaf)) {
                N node = adapter.apply(model);
                childrenSetter.accept(node, childNodes);
                result.add(node);
            }
        }

        // 排序
        if (comparator != null) {
            result.sort(comparator);
        }

        // 递归结束回溯
        if (parentId == null) {
            visitingNull = false;
        } else {
            visiting.remove(parentId);
        }

        return result;
    }

    // =================================InnerClass============================================
    /**
     * 过滤器接口
     * @param <T> 原始数据类型
     */
    @FunctionalInterface
    public interface NodeFilter<T> {

        boolean accept(T model, int depth, boolean isLeaf);

        default NodeFilter<T> and(NodeFilter<T> other) {
            Objects.requireNonNull(other);
            return (model, depth, leaf) -> this.accept(model, depth, leaf) && other.accept(model, depth, leaf);
        }
    }
}