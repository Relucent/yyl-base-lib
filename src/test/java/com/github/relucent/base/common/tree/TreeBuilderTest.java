package com.github.relucent.base.common.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.github.relucent.base.common.tree.TreeBuildRule.CyclePolicy;

public class TreeBuilderTest {

	// 示例数据结构
	static class Node {
		Integer id;
		Integer parentId;
		String name;
		List<Node> children;

		Node(Integer id, Integer parentId, String name) {
			this.id = id;
			this.parentId = parentId;
			this.name = name;
		}
	}

	@Test
	public void testBasicTreeBuild() {
		List<Node> data = Arrays.asList(//
				new Node(1, 0, "A"), //
				new Node(2, 1, "B"), //
				new Node(3, 1, "C")//
		);

		List<Node> tree = TreeBuilder.<Node, Node, Integer>builder()//
				.data(data)//
				.rootParentId(0)//
				.idGetter(n -> n.id)//
				.parentIdGetter(n -> n.parentId)//
				.nodeAdapter(n -> n)//
				.childrenSetter((n, c) -> n.children = c)//
				.build();

		assertEquals(1, tree.size());
		assertEquals(2, tree.get(0).children.size());
	}

	@Test
	public void testCycleDetection() {
		List<Node> data = Arrays.asList(//
				new Node(1, 2, "A"), //
				new Node(2, 1, "B")//
		);
		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> //
		TreeBuilder.<Node, Node, Integer>builder()//
				.data(data)//
				.rootParentId(1) // 从 B 的父 ID = 2 开始递归
				.idGetter(n -> n.id)//
				.parentIdGetter(n -> n.parentId)//
				.nodeAdapter(n -> n)//
				.childrenSetter((n, c) -> n.children = c)//
				.cyclePolicy(CyclePolicy.ERROR)//
				.build()//
		);
		assertTrue(ex.getMessage().toLowerCase().contains("cycle"));
	}

	@Test
	public void testFilter() {

		List<Node> data = Arrays.asList(//
				new Node(1, 0, "A"), //
				new Node(2, 1, "B"), //
				new Node(3, 1, "C")//
		);

		List<Node> tree = TreeBuilder.<Node, Node, Integer>builder()//
				.data(data)//
				.rootParentId(0)//
				.nodeFilter((node, depth, leaf) -> !"B".equals(node.name))//
				.idGetter(n -> n.id)//
				.parentIdGetter(n -> n.parentId)//
				.nodeAdapter(n -> n)//
				.childrenSetter((n, c) -> n.children = c)//
				.build();

		assertEquals(1, tree.get(0).children.size());
		assertEquals("C", tree.get(0).children.get(0).name);
	}

	@Test
	public void testComparator() {
		List<Node> data = Arrays.asList(new Node(1, 0, "Root"), //
				new Node(3, 1, "C"), //
				new Node(2, 1, "B")//
		);

		List<Node> tree = TreeBuilder.<Node, Node, Integer>builder()//
				.data(data)//
				.rootParentId(0)//
				.idGetter(n -> n.id)//
				.parentIdGetter(n -> n.parentId)//
				.nodeAdapter(n -> n)//
				.childrenSetter((n, c) -> n.children = c)//
				.comparator(Comparator.comparing(n -> n.name))//
				.build();

		assertEquals("B", tree.get(0).children.get(0).name);
	}

	@Test
	public void testReuseBuilderAfterException() {
		TreeBuilder<Node, Node, Integer> builder = TreeBuilder.<Node, Node, Integer>builder()//
				.idGetter(n -> n.id)//
				.parentIdGetter(n -> n.parentId)//
				.nodeAdapter(n -> n)//
				.childrenSetter((n, c) -> n.children = c)//
				.cyclePolicy(CyclePolicy.ERROR);

		// 第一次：含环数据，应抛异常
		List<Node> cyclic = Arrays.asList(new Node(1, 2, "A"), new Node(2, 1, "B"));
		assertThrows(IllegalStateException.class, () -> builder.data(cyclic).rootParentId(1).build());

		// 第二次：复用同一实例、换无环数据，循环检测状态不应残留
		List<Node> good = Arrays.asList(new Node(1, 0, "A"), new Node(2, 1, "B"));
		List<Node> tree = builder.data(good).rootParentId(0).build();
		assertEquals(1, tree.size());
		assertEquals(1, tree.get(0).children.size());
	}

	@Test
	public void testReuseBuilderMultipleBuilds() {
		TreeBuilder<Node, Node, Integer> builder = TreeBuilder.<Node, Node, Integer>builder()//
				.idGetter(n -> n.id)//
				.parentIdGetter(n -> n.parentId)//
				.nodeAdapter(n -> n)//
				.childrenSetter((n, c) -> n.children = c);

		List<Node> d1 = Arrays.asList(new Node(1, 0, "A"), new Node(2, 1, "B"));
		List<Node> d2 = Arrays.asList(new Node(3, 0, "C"), new Node(4, 3, "D"));

		List<Node> t1 = builder.data(d1).rootParentId(0).build();
		List<Node> t2 = builder.data(d2).rootParentId(0).build();

		assertEquals(1, t1.size());
		assertEquals(1, t2.size());
		assertEquals(Integer.valueOf(3), t2.get(0).id);
	}

	@Test
	public void testVeryDeepTreeNoStackOverflow() {
		// 构造 10000 层链：1->2->...->10000，rootParentId=0
		// 递归实现在超深数据下会抛出 StackOverflowError，当前使用迭代实现应正常完成
		int depth = 10000;
		List<Node> data = new ArrayList<>(depth);
		data.add(new Node(1, 0, "root"));
		for (int i = 2; i <= depth; i++) {
			data.add(new Node(i, i - 1, "n" + i));
		}

		List<Node> tree = TreeBuilder.<Node, Node, Integer>builder()//
				.data(data)//
				.rootParentId(0)//
				.idGetter(n -> n.id)//
				.parentIdGetter(n -> n.parentId)//
				.nodeAdapter(n -> n)//
				.childrenSetter((n, c) -> n.children = c)//
				.build();

		assertEquals(1, tree.size());
		Node cursor = tree.get(0);
		int levels = 1;
		while (cursor.children != null && !cursor.children.isEmpty()) {
			cursor = cursor.children.get(0);
			levels++;
		}
		assertEquals(depth, levels);
	}
}
