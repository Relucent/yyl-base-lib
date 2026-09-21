package com.github.relucent.base.common.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.relucent.base.common.tree.TreeBuildRule.NodeFilter;

/**
 * Tree类型工具类
 * @see TreeUtil
 * @author YYL
 */
public class TreeUtil {

	// =================================Constants=============================================
	private static final String PATH_SEPARATOR = "/";

	// =================================Fields================================================
	// ...

	// =================================Constructors===========================================
	/**
	 * 工具类方法，实例不应在标准编程中构造。
	 */
	protected TreeUtil() {
	}

	// =================================Methods================================================
	/**
	 * 构建树模型
	 * @param <T>      原始数据类型泛型
	 * @param <N>      树节点类型泛型
	 * @param <I>      ID类型泛型
	 * @param data     数据
	 * @param parentId 父节点ID
	 * @param rule     构建规则
	 * @return 树模型（根节点列表）
	 */
	public static <T, N, I> List<N> buildTree(List<T> data, I parentId, TreeBuildRule<T, N, I> rule) {
		Objects.requireNonNull(rule, "rule cannot be null");
		return TreeBuilder.<T, N, I>builder()//
				.data(data)//
				.rootParentId(parentId)//
				.idGetter(rule.getIdGetter())//
				.parentIdGetter(rule.getParentIdGetter())//
				.nodeAdapter(rule.getAdapter())//
				.childrenSetter(rule.getChildrenSetter())//
				.nodeFilter(rule.getFilter())//
				.comparator(rule.getComparator())//
				.cyclePolicy(rule.getCyclePolicy())//
				.build();
	}

	/**
	 * 重新构建 ID_PATH<br>
	 * 基于 parentId 构建索引后做深度优先遍历（时间复杂度 O(n)），同时采用显式栈避免超深数据递归导致的 {@link StackOverflowError}。<br>
	 * 注意：<br>
	 * 1、传入的 {@code nodes} 应当无环，若含环环点子树将被截断。 <br>
	 * 2、参数 parentId 可以为 {@code null}，顶层节点parentId为{@code null}的情况。 <br>
	 * 3、对于parentId 无法遍历到的节点是孤立节点，不会被处理。<br>
	 * @param <N>            节点对象泛型
	 * @param <I>            ID 类型泛型
	 * @param nodes          节点列表（以 parentId 可索引的扁平集合）
	 * @param idGetter       节点 ID 访问器
	 * @param parentIdGetter 节点父 ID 访问器
	 * @param idPathSetter   ID_PATH 设置器
	 * @param parentId       上级节点 ID，从该节点向下展开
	 * @param parentIdPath   上级节点 ID_PATH，作为本层路径前缀；会被规范化为首尾均含 {@code '/'} 的绝对路径前缀（{@code null} 视为 {@code '/'}）
	 */
	public static <N, I> void rebuildIdPath(//
			Collection<N> nodes, //
			Function<N, I> idGetter, //
			Function<N, I> parentIdGetter, //
			BiConsumer<N, String> idPathSetter, //
			I parentId, //
			String parentIdPath) {

		// 规范化为绝对路径前缀：null 视为根前缀 '/'；非空时若缺失前导或尾随分隔符则补齐全，
		// 避免被直接拼接（例如 "ROOT" + "100" = "ROOT100/"）
		if (parentIdPath == null) {
			parentIdPath = PATH_SEPARATOR;
		} else {
			if (!parentIdPath.startsWith(PATH_SEPARATOR)) {
				parentIdPath = PATH_SEPARATOR + parentIdPath;
			}
			if (!parentIdPath.endsWith(PATH_SEPARATOR)) {
				parentIdPath += PATH_SEPARATOR;
			}
		}

		// parentId -> 子节点
		Map<I, List<N>> childrenMap = new HashMap<>();
		for (N node : nodes) {
			childrenMap.computeIfAbsent(parentIdGetter.apply(node), k -> new ArrayList<>()).add(node);
		}

		// id -> ID_PATH
		Map<I, String> pathMap = new HashMap<>();

		// 已压栈的子节点 ID：重复 ID 仅首个生效，防止重复 ID 作为父节点时其孙节点路径被后写覆盖（产生错误路径）
		Set<I> pushedIds = new HashSet<>();

		// 待展开的 parentId
		// ArrayDeque 不允许 null，因此 parentId 不直接入栈。
		Deque<I> parentIdStack = new ArrayDeque<>();

		// 第一次直接处理传入的 parentId。
		// parentId 可以为 null，因此不能直接放入 ArrayDeque。
		I currentParentId = parentId;
		String currentParentPath = parentIdPath;

		while (true) {

			// 取出并移除当前 parentId 对应的子节点。
			// remove() 同时保证同一个 parentId 不会被重复展开。
			List<N> children = childrenMap.remove(currentParentId);

			if (children != null) {

				// 逆序入栈，保证子节点设置顺序与 children 原始顺序一致（LIFO 弹出）
				for (int i = children.size() - 1; i >= 0; i--) {

					N child = children.get(i);
					I childId = idGetter.apply(child);

					// 重复 ID / null ID 仅首个生效：跳过后续重复节点（不设置路径、不展开），
					// 防止重复 ID 作为父节点时其孙节点路径被后写覆盖而产生错误路径
					if (childId == null || !pushedIds.add(childId)) {
						continue;
					}

					String childPath = currentParentPath + childId + PATH_SEPARATOR;

					// 设置当前节点 ID_PATH
					idPathSetter.accept(child, childPath);

					// 保存当前节点路径，供下一层使用
					pathMap.put(childId, childPath);

					parentIdStack.push(childId);
				}
			}

			// 没有待展开节点，遍历结束
			if (parentIdStack.isEmpty()) {
				break;
			}

			// 继续展开下一层
			currentParentId = parentIdStack.pop();
			currentParentPath = pathMap.get(currentParentId);
		}

		// childrenMap 中剩余的节点，就是从指定 parentId 无法遍历到的节点。
		// 后续如果需要处理孤立节点，可直接基于这里扩展。
	}

	/**
	 * 从指定的节点开始，以深度优先的前序方式遍历树。<br>
	 * 每访问一个节点，都会调用 {@code action} 对该节点进行处理。<br>
	 * @param <N>            节点对象泛型
	 * @param nodes          遍历的起始节点，可以指定多个节点
	 * @param childrenGetter 子节点访问器（返回某节点的直接子节点集合，{@code null} 表示无子节点）
	 * @param action         用于处理遍历节点的操作，不能为 {@code null}
	 */
	public static <N> void walkTree(Collection<N> nodes, Function<N, ? extends Collection<N>> childrenGetter,
			Consumer<N> action) {

		Objects.requireNonNull(childrenGetter, "childrenGetter cannot be null");

		// 传入起始节点为空
		if (nodes == null || nodes.isEmpty()) {
			return;
		}

		// 显式栈：用于替代递归
		Deque<N> stack = new ArrayDeque<>();

		// 已访问节点集合，防止图结构（含环）导致的无限递归
		Set<N> visited = new HashSet<>();

		// 逆序入栈，保证弹出顺序与 nodes 原始顺序一致（栈为 LIFO）
		List<N> rootList = new ArrayList<>(nodes);
		for (int i = rootList.size() - 1; i >= 0; i--) {
			N root = rootList.get(i);
			if (root != null) {
				stack.push(root);
			}
		}

		while (!stack.isEmpty()) {
			// 弹出栈顶节点，先访问自身（前序）
			N node = stack.pop();

			// 跳过已访问节点（环/重复引用保护）
			if (!visited.add(node)) {
				continue;
			}

			action.accept(node);

			// 将其子节点逆序入栈，保证弹出时按原始顺序处理
			Collection<N> children = childrenGetter.apply(node);
			if (children != null && !children.isEmpty()) {
				List<N> childList = new ArrayList<>(children);
				for (int i = childList.size() - 1; i >= 0; i--) {
					N child = childList.get(i);
					if (child != null && !visited.contains(child)) {
						stack.push(child);
					}
				}
			}
		}
	}

	/**
	 * 构建树模型
	 * @param <T>            原始数据类型泛型
	 * @param <N>            树节点对象泛型
	 * @param <I>            ID类型泛型
	 * @param parentId       父节点ID
	 * @param data           数据
	 * @param adapter        节点适配器
	 * @param idGetter       节点ID访问器
	 * @param parentIdGetter 节点父ID访问器
	 * @param childrenSetter 子节点设置器
	 * @return 树模型
	 * @deprecated {@link #buildTree(List, Object, TreeBuildRule)}
	 */
	public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter,
			IdGetter<T, I> idGetter, ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter) {
		return buildTree(parentId, data, adapter, null, idGetter, parentIdGetter, childrenSetter, null);
	}

	/**
	 * 构建树模型
	 * @param <T>            原始数据类型泛型
	 * @param <N>            树节点对象泛型
	 * @param <I>            ID类型泛型
	 * @param parentId       父节点ID
	 * @param data           数据
	 * @param adapter        节点适配器
	 * @param idGetter       节点ID访问器
	 * @param parentIdGetter 节点父ID访问器
	 * @param childrenSetter 子节点设置器
	 * @param comparator     排序比较器
	 * @return 树模型
	 * @deprecated {@link #buildTree(List, Object, TreeBuildRule)}
	 */
	public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter,
			IdGetter<T, I> idGetter, ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter,
			Comparator<N> comparator) {
		return buildTree(parentId, data, adapter, null, idGetter, parentIdGetter, childrenSetter, comparator);
	}

	/**
	 * 构建树模型
	 * @param <T>            原始数据类型泛型
	 * @param <N>            树节点对象泛型
	 * @param <I>            ID类型泛型
	 * @param parentId       父节点ID
	 * @param data           数据
	 * @param adapter        节点适配器
	 * @param filter         节点过滤器
	 * @param idGetter       节点ID访问器
	 * @param parentIdGetter 节点父ID访问器
	 * @param childrenSetter 子节点设置器
	 * @return 树模型
	 * @deprecated {@link #buildTree(List, Object, TreeBuildRule)}
	 */
	public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter, NodeFilter<T> filter,
			IdGetter<T, I> idGetter, ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter) {
		return buildTree(parentId, data, adapter, filter, idGetter, parentIdGetter, childrenSetter, null);
	}

	/**
	 * 构建树模型
	 * @param <T>            原始数据类型泛型
	 * @param <N>            树节点类型泛型
	 * @param <I>            ID类型泛型
	 * @param parentId       父节点ID
	 * @param data           数据
	 * @param adapter        节点适配器
	 * @param filter         节点过滤器
	 * @param idGetter       节点ID访问器
	 * @param parentIdGetter 节点父ID访问器
	 * @param childrenSetter 子节点设置器
	 * @param comparator     排序比较器（{@code null} 表示不排序）
	 * @return 树模型
	 * @deprecated 请使用 {@link #buildTree(List, Object, TreeBuildRule)}
	 */
	public static <T, N, I> List<N> buildTree(I parentId, List<T> data, NodeAdapter<T, N> adapter, NodeFilter<T> filter,
			IdGetter<T, I> idGetter, ParentIdGetter<T, I> parentIdGetter, ChildrenSetter<N> childrenSetter,
			Comparator<N> comparator) {
		return TreeBuilder.<T, N, I>builder()//
				.data(data)//
				.rootParentId(parentId)//
				.idGetter(idGetter::getId)//
				.parentIdGetter(parentIdGetter::getParentId)//
				.nodeAdapter(adapter::adapt)//
				.childrenSetter(childrenSetter::setChildren)//
				.nodeFilter(filter::test)//
				.comparator(comparator)//
				.build();
	}

	/**
	 * 重新构建 ID_PATH<br>
	 * @param <N>            节点对象泛型
	 * @param <I>            ID类型泛型
	 * @param nodes          节点列表
	 * @param idGetter       节点ID访问器
	 * @param parentIdGetter 节点父ID访问器
	 * @param idPathSetter   ID路径设置器
	 * @param parentId       上级ID
	 * @param parentIdPath   上级ID路径
	 * @deprecated {@link #rebuildIdPath}
	 */
	public static <N, I> void recursiveSetIdPath(Collection<N> nodes, IdGetter<N, I> idGetter,
			ParentIdGetter<N, I> parentIdGetter, IdPathSetter<N> idPathSetter, I parentId, String parentIdPath) {
		for (N node : nodes) {
			if (Objects.equals(parentId, parentIdGetter.getParentId(node))) {
				I id = idGetter.getId(node);
				String idPath = parentIdPath + id + PATH_SEPARATOR;
				idPathSetter.setIdPath(node, idPath);
				recursiveSetIdPath(nodes, idGetter, parentIdGetter, idPathSetter, id, idPath);
			}
		}
	}

	// =================================RuleClass=============================================

	/**
	 * 数据ID访问器<br>
	 * 已废弃：新代码请改用 {@link TreeBuildRule} 配合 {@link java.util.function.Function} 定义访问器。<br>
	 * @param <T> 数据对象泛型
	 * @param <I> ID类型泛型
	 * @deprecated 请使用 {@link TreeBuildRule} 与 {@link java.util.function.Function}
	 */
	@Deprecated
	@FunctionalInterface
	public static interface IdGetter<T, I> {
		/**
		 * 获得ID
		 * @param model 模型对象
		 * @return ID
		 */
		@Deprecated
		I getId(T model);
	}

	/**
	 * 父ID访问器 已废弃
	 * @param <T> 数据对象泛型
	 * @param <I> ID类型泛型
	 */
	@Deprecated
	@FunctionalInterface
	public static interface ParentIdGetter<T, I> {
		/**
		 * 获得父节点ID
		 * @param model 模型对象
		 * @return 上级ID
		 */
		@Deprecated
		I getParentId(T model);
	}

	/**
	 * 子节点设置器<br>
	 * 已废弃 新代码请改用 {@link TreeBuildRule} 配合 {@link java.util.function.BiConsumer} 定义设置器。<br>
	 * @param <N> 节点对象泛型
	 * @deprecated 请使用 {@link TreeBuildRule} 与 {@link java.util.function.BiConsumer}
	 */
	@Deprecated
	@FunctionalInterface
	public static interface ChildrenSetter<N> {
		/**
		 * 设置子节点
		 * @param node     节点对象
		 * @param children 子节点
		 */
		@Deprecated
		void setChildren(N node, List<N> children);
	}

	/**
	 * 节点ID设置器 <br>
	 * 已废弃： 新代码请改用 {@link java.util.function.BiConsumer} 定义设置器。<br>
	 * @param <N> 节点对象泛型
	 * @deprecated 请使用 {@link java.util.function.BiConsumer}
	 */
	@Deprecated
	@FunctionalInterface
	public static interface IdPathSetter<N> {
		/**
		 * ID路径
		 * @param node   节点对象
		 * @param idPath ID路径
		 */
		@Deprecated
		void setIdPath(N node, String idPath);
	}

	/**
	 * 节点适配器<br>
	 * 已废弃： 新代码请改用 {@link TreeBuildRule} 配合 {@link java.util.function.Function} 定义适配器。<br>
	 * @param <T> 模型对象类型泛型
	 * @param <N> 节点对象类型泛型
	 * @deprecated 请使用 {@link TreeBuildRule} 与 {@link java.util.function.Function}
	 */
	@Deprecated
	@FunctionalInterface
	public static interface NodeAdapter<T, N> {
		/**
		 * 将数据模型转换为树节点模型
		 * @param model 数据模型
		 * @return 树节点模型
		 */
		@Deprecated
		N adapt(T model);
	}
}
