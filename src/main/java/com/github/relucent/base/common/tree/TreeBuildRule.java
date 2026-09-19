package com.github.relucent.base.common.tree;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 树构建规则<br>
 * 用于定义树结构构建所需的规则和处理方式， 规则创建完成后可重复用于多次树构建，由 {@code TreeUtil} 根据规则和实际数据构建树。<br>
 * 必填规则包括节点 ID、父节点 ID、节点转换器以及子节点设置器； 同时支持节点排序、节点过滤以及循环引用处理策略等可选配置。<br>
 * {@code TreeBuildRule} 不保存具体的业务数据和根节点 ID，因此可以作为不可变配置对象安全地复用。<br>
 * @see TreeUtil#buildTree(List, Object, TreeBuildRule)
 * @param <T> 原始数据类型
 * @param <N> 树节点类型
 * @param <I> ID 类型
 */
public final class TreeBuildRule<T, N, I> {

	// =================================Fields================================================

	/** 节点 ID 获取器 */
	private final Function<T, I> idGetter;

	/** 父节点 ID 获取器 */
	private final Function<T, I> parentIdGetter;

	/** 原始数据转换为树节点 */
	private final Function<T, N> adapter;

	/** 子节点设置器 */
	private final BiConsumer<N, List<N>> childrenSetter;

	/** 节点排序器 */
	private final Comparator<N> comparator;

	/** 节点过滤器 */
	private final NodeFilter<T> filter;

	/** 循环引用处理策略 */
	private final CyclePolicy cyclePolicy;

	// =================================Constructors===========================================
	private TreeBuildRule(Function<T, I> idGetter, Function<T, I> parentIdGetter, Function<T, N> adapter,
			BiConsumer<N, List<N>> childrenSetter, Comparator<N> comparator, NodeFilter<T> filter,
			CyclePolicy cyclePolicy) {

		this.idGetter = idGetter;
		this.parentIdGetter = parentIdGetter;
		this.adapter = adapter;
		this.childrenSetter = childrenSetter;
		this.comparator = comparator;
		this.filter = filter;
		this.cyclePolicy = cyclePolicy;
	}

	// =================================Builder================================================
	/**
	 * 创建树构建规则
	 *
	 * @param <T> 原始数据类型
	 * @param <N> 树节点类型
	 * @param <I> ID 类型
	 * @return Builder
	 */
	public static <T, N, I> IdStep<T, N, I> builder() {
		return new Builder<>();
	}

	// =================================Methods================================================
	Function<T, I> getIdGetter() {
		return idGetter;
	}

	Function<T, I> getParentIdGetter() {
		return parentIdGetter;
	}

	Function<T, N> getAdapter() {
		return adapter;
	}

	BiConsumer<N, List<N>> getChildrenSetter() {
		return childrenSetter;
	}

	Comparator<N> getComparator() {
		return comparator;
	}

	NodeFilter<T> getFilter() {
		return filter;
	}

	CyclePolicy getCyclePolicy() {
		return cyclePolicy;
	}

	// =================================StepClass=============================================
	/**
	 * ID 设置阶段
	 */
	public interface IdStep<T, N, I> {
		ParentIdStep<T, N, I> id(Function<T, I> idGetter);
	}

	/**
	 * 父节点 ID 设置阶段
	 */
	public interface ParentIdStep<T, N, I> {
		AdapterStep<T, N, I> parentId(Function<T, I> parentIdGetter);
	}

	/**
	 * 节点转换设置阶段
	 */
	public interface AdapterStep<T, N, I> {
		ChildrenStep<T, N, I> adapter(Function<T, N> adapter);
	}

	/**
	 * 子节点设置阶段
	 */
	public interface ChildrenStep<T, N, I> {
		OptionalStep<T, N, I> children(BiConsumer<N, List<N>> childrenSetter);
	}

	/**
	 * 可选配置阶段
	 */
	public interface OptionalStep<T, N, I> {

		/**
		 * 设置节点排序器
		 *
		 * @param comparator 排序器
		 * @return 当前 Builder
		 */
		OptionalStep<T, N, I> comparator(Comparator<N> comparator);

		/**
		 * 设置节点过滤器
		 *
		 * @param filter 节点过滤器
		 * @return 当前 Builder
		 */
		OptionalStep<T, N, I> nodeFilter(NodeFilter<T> filter);

		/**
		 * 设置循环引用处理策略
		 *
		 * @param cyclePolicy 循环引用处理策略
		 * @return 当前 Builder
		 */
		OptionalStep<T, N, I> cyclePolicy(CyclePolicy cyclePolicy);

		/**
		 * 创建树构建规则
		 * @return 树构建规则
		 */
		TreeBuildRule<T, N, I> build();
	}

	// =================================BuilderClass==========================================
	/**
	 * Builder 实现，通过不同阶段接口保证必填参数不会遗漏
	 */
	private static final class Builder<T, N, I> implements IdStep<T, N, I>, ParentIdStep<T, N, I>, AdapterStep<T, N, I>,
			ChildrenStep<T, N, I>, OptionalStep<T, N, I> {

		private Function<T, I> idGetter;
		private Function<T, I> parentIdGetter;
		private Function<T, N> adapter;
		private BiConsumer<N, List<N>> childrenSetter;

		private Comparator<N> comparator = null;
		private NodeFilter<T> filter = (model, depth, isLeaf) -> true;
		private CyclePolicy cyclePolicy = CyclePolicy.SKIP;

		@Override
		public ParentIdStep<T, N, I> id(Function<T, I> idGetter) {
			this.idGetter = Objects.requireNonNull(idGetter, "idGetter");
			return this;
		}

		@Override
		public AdapterStep<T, N, I> parentId(Function<T, I> parentIdGetter) {
			this.parentIdGetter = Objects.requireNonNull(parentIdGetter, "parentIdGetter");
			return this;
		}

		@Override
		public ChildrenStep<T, N, I> adapter(Function<T, N> adapter) {
			this.adapter = Objects.requireNonNull(adapter, "adapter");
			return this;
		}

		@Override
		public OptionalStep<T, N, I> children(BiConsumer<N, List<N>> childrenSetter) {
			this.childrenSetter = Objects.requireNonNull(childrenSetter, "childrenSetter");
			return this;
		}

		@Override
		public OptionalStep<T, N, I> comparator(Comparator<N> comparator) {
			this.comparator = comparator;
			return this;
		}

		@Override
		public OptionalStep<T, N, I> nodeFilter(NodeFilter<T> filter) {
			this.filter = filter == null ? (model, depth, isLeaf) -> true : filter;
			return this;
		}

		@Override
		public OptionalStep<T, N, I> cyclePolicy(CyclePolicy cyclePolicy) {
			this.cyclePolicy = cyclePolicy == null ? CyclePolicy.SKIP : cyclePolicy;
			return this;
		}

		@Override
		public TreeBuildRule<T, N, I> build() {
			return new TreeBuildRule<>(idGetter, parentIdGetter, adapter, childrenSetter, comparator, filter,
					cyclePolicy);
		}
	}

	// =================================RuleClass=============================================
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
		 * @param leaf  是否叶子节点
		 * @return 如果树中不包含该节点，则返回true，否则返回false。
		 */
		boolean test(T model, int depth, boolean leaf);

		/**
		 * 创建联合条件过滤器
		 * @param other 节点过滤器
		 * @return 联合条件节点过滤器
		 */
		default NodeFilter<T> and(NodeFilter<? super T> other) {
			Objects.requireNonNull(other, "other");
			return (model, depth, leaf) -> this.test(model, depth, leaf) && other.test(model, depth, leaf);
		}
	}

	/**
	 * 循环检测策略
	 */
	public static enum CyclePolicy {
		/** 直接跳过循环节点（默认） */
		SKIP,
		/** 检测到循环即抛异常 */
		ERROR;
	}
}