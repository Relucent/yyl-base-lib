package com.github.relucent.base.common.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.github.relucent.base.common.tree.TreeBuildRule.CyclePolicy;
import com.github.relucent.base.common.tree.TreeBuildRule.NodeFilter;

/**
 * 树结构构建工具类<br>
 * @param <T> 原始数据类型（例如数据库表实体）
 * @param <N> 树节点类型（构建后的节点对象）
 * @param <I> ID 类型（节点唯一标识，例如 Integer, Long, String）
 * @author YYL
 */
public class TreeBuilder<T, N, I> {

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
	 * @return 树结构
	 */
	public List<N> build() {

		// 参数校验
		validate();

		// 构建数据索引（parentId → 子节点数据）
		Map<I, List<T>> parentToChildrenIndex = buildParentToChildrenIndex();

		// 当前 DFS 路径上的 parentId，用于检测循环（HashSet 允许 null）
		Set<I> visiting = new HashSet<>();

		// 使用显式栈代替递归，避免超深树触发 StackOverflowError
		Deque<Frame<I, N, T>> stack = new ArrayDeque<>();

		// 第一层压入栈（第一层树节点）
		stack.push(new Frame<>(//
				rootParentId, //
				0, //
				parentToChildrenIndex.getOrDefault(rootParentId, Collections.emptyList())//
		));

		while (!stack.isEmpty()) {
			Frame<I, N, T> frame = stack.peek();

			// 第一次进入当前层
			if (frame.enter()) {
				if (!visiting.add(frame.parentId)) {
					handleCycle(frame, stack);
					continue;
				}
			}

			// 还有子节点没有展开
			if (frame.hasNextChild()) {
				T model = frame.nextChild();
				I id = idGetter.apply(model);
				stack.push(new Frame<>(//
						id, //
						frame.depth + 1, //
						parentToChildrenIndex.getOrDefault(id, Collections.emptyList())//
				));
				continue;
			}

			// 当前层所有子节点都处理完成
			List<N> result = buildNodes(frame);

			// 当前层处理完成，从循环检测路径中移除
			visiting.remove(frame.parentId);

			stack.pop();

			// 当前 Frame 就是根节点
			if (stack.isEmpty()) {
				return result;
			}

			// 将当前层的结果交给父 Frame
			stack.peek().addChildResult(result);
		}

		return Collections.emptyList();
	}

	/**
	 * 参数校验
	 */
	private void validate() {
		Objects.requireNonNull(data, "data cannot be null");
		Objects.requireNonNull(idGetter, "idGetter cannot be null");
		Objects.requireNonNull(parentIdGetter, "parentIdGetter cannot be null");
		Objects.requireNonNull(adapter, "adapter cannot be null");
		Objects.requireNonNull(childrenSetter, "childrenSetter cannot be null");
	}

	/**
	 * 构建 父ID → 子节点数据索引
	 * @return 父ID → 子节点数据索引
	 */
	private Map<I, List<T>> buildParentToChildrenIndex() {
		Map<I, List<T>> index = new HashMap<>();
		for (T model : data) {
			I parentId = parentIdGetter.apply(model);
			index.computeIfAbsent(parentId, k -> new ArrayList<>()).add(model);
		}
		return index;
	}

	/**
	 * 处理循环引用
	 * @param frame 当前栈帧
	 * @param stack 显式栈
	 */
	private void handleCycle(Frame<I, N, T> frame, Deque<Frame<I, N, T>> stack) {

		switch (cyclePolicy) {
		case ERROR:
			throw new IllegalStateException("Cycle detected at parentId=" + frame.parentId);

		case SKIP:
			// 当前节点跳过，认为它没有子节点（pop弹出栈顶元素）
			stack.pop();

			if (!stack.isEmpty()) {
				// 设置这个层级帧添加返回为空集合（没有子元素）
				// 注：peek() 查看栈顶元素，但并不从栈中移除该元素
				stack.peek().addChildResult(Collections.emptyList());
			}
			break;

		default:
			throw new IllegalStateException("Unsupported cycle policy: " + cyclePolicy);
		}
	}

	/**
	 * 构建当前层的树节点
	 * @param frame 当前栈帧
	 * @return 树节点
	 */
	private List<N> buildNodes(Frame<I, N, T> frame) {

		List<N> result = new ArrayList<>(frame.childrenData.size());

		for (int i = 0; i < frame.childrenData.size(); i++) {
			T model = frame.childrenData.get(i);

			// 当前 model 对应的子树
			List<N> children = frame.childResults.get(i);

			boolean isLeaf = children.isEmpty();

			if (!filter.test(model, frame.depth, isLeaf)) {
				continue;
			}

			N node = adapter.apply(model);

			childrenSetter.accept(node, children);

			result.add(node);
		}

		// 当前层排序
		if (comparator != null) {
			result.sort(comparator);
		}

		return result;
	}

	// =================================InnerClass=============================================
	/**
	 * 迭代遍历的栈帧
	 * @param <I> ID 类型
	 * @param <N> 树节点类型
	 * @param <T> 原始数据类型
	 */
	private static final class Frame<I, N, T> {

		/** 当前层的 parentId */
		final I parentId;
		/** 当前深度 */
		final int depth;
		/** 当前 parentId 下的所有子节点原始数据 */
		final List<T> childrenData;
		/** 当前正在处理的子节点下标 */
		int index;
		/** 每个 childrenData 对应的子树结果。 */
		final List<List<N>> childResults;
		/** 是否已经进入过当前 Frame */
		boolean entered;

		Frame(I parentId, int depth, List<T> childrenData) {
			this.parentId = parentId;
			this.depth = depth;
			this.childrenData = childrenData;
			this.childResults = new ArrayList<>(childrenData.size());
		}

		/**
		 * 第一次进入 Frame
		 * @return 第一次调用返回 true，后续返回 false
		 */
		boolean enter() {
			if (entered) {
				return false;
			}
			entered = true;
			return true;
		}

		/**
		 * 是否还有未处理的子节点
		 * @return 如果有未处理的子节点返回true
		 */
		boolean hasNextChild() {
			return index < childrenData.size();
		}

		/**
		 * 获取下一个待处理的子节点数据
		 * @return 节点数据
		 */
		T nextChild() {
			return childrenData.get(index++);
		}

		/**
		 * 接收一个子树处理结果
		 * @param 子树处理结果
		 */
		void addChildResult(List<N> result) {
			childResults.add(result);
		}
	}
}