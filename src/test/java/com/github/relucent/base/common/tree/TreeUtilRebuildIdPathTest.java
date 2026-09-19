package com.github.relucent.base.common.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link TreeUtil#rebuildIdPath} 的单元测试。<br>
 * 覆盖：基础树、绝对前缀、子树重锚、空子树早退、超深链（防 StackOverflow）、环（防无限循环）、重复 ID（首个生效）。<br>
 * 路径约定：本方法输出首尾均含 {@code '/'} 的绝对路径，例如 {@code /100/110/}。
 */
public class TreeUtilRebuildIdPathTest {

	// =================================TestFixture============================================

	/** 测试节点：同时持有 id / parentId / idPath（idPath 用于记录重建后的路径） */
	private static class PathNode {

		final String id;
		final String parentId;
		String idPath;

		PathNode(String id, String parentId) {
			this.id = id;
			this.parentId = parentId;
		}
	}

	/** 标准样例：100(null) -> {110,120}; 110 -> {111,112}; 120 -> {121,122} */
	private static List<PathNode> sample() {
		List<PathNode> list = new ArrayList<>();
		list.add(new PathNode("100", null));
		list.add(new PathNode("110", "100"));
		list.add(new PathNode("111", "110"));
		list.add(new PathNode("112", "110"));
		list.add(new PathNode("120", "100"));
		list.add(new PathNode("121", "120"));
		list.add(new PathNode("122", "120"));
		return list;
	}

	private static PathNode find(Collection<PathNode> nodes, String id) {
		for (PathNode n : nodes) {
			if (id.equals(n.id)) {
				return n;
			}
		}
		throw new AssertionError("node not found: " + id);
	}

	// =================================Tests==================================================

	/**
	 * 基础树：从 parentId=null 出发，parentIdPath="" 时，路径应为绝对格式 "/id/..." 且逐层拼接正确。
	 */
	@Test
	public void testBasicTree() {
		List<PathNode> nodes = sample();
		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, "");

		Assert.assertEquals("/100/", find(nodes, "100").idPath);
		Assert.assertEquals("/100/110/", find(nodes, "110").idPath);
		Assert.assertEquals("/100/110/111/", find(nodes, "111").idPath);
		Assert.assertEquals("/100/110/112/", find(nodes, "112").idPath);
		Assert.assertEquals("/100/120/", find(nodes, "120").idPath);
		Assert.assertEquals("/100/120/121/", find(nodes, "121").idPath);
		Assert.assertEquals("/100/120/122/", find(nodes, "122").idPath);
	}

	/**
	 * 带前缀：parentIdPath="ROOT/" 时，所有路径应带该绝对前缀。
	 */
	@Test
	public void testWithPrefix() {
		List<PathNode> nodes = sample();
		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, "ROOT/");

		Assert.assertEquals("/ROOT/100/", find(nodes, "100").idPath);
		Assert.assertEquals("/ROOT/100/110/", find(nodes, "110").idPath);
		Assert.assertEquals("/ROOT/100/110/111/", find(nodes, "111").idPath);
	}

	/**
	 * 子树重锚：从 parentId="100" 出发、parentIdPath="100/" 时，仅重建 100 的子树，路径以 "/100/110/" 等开头。
	 */
	@Test
	public void testStartFromSubtree() {
		List<PathNode> nodes = sample();
		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, "100", "100/");

		// 100 本身不是起点节点，不应被处理
		Assert.assertNull(find(nodes, "100").idPath);
		Assert.assertEquals("/100/110/", find(nodes, "110").idPath);
		Assert.assertEquals("/100/110/111/", find(nodes, "111").idPath);
		Assert.assertEquals("/100/120/121/", find(nodes, "121").idPath);
	}

	/**
	 * 空子树早退：parentId 在数据中没有子节点时，方法应直接返回，不调用 idPathSetter（idPath 保持初始 null）。
	 */
	@Test
	public void testNoChildrenEarlyReturn() {
		List<PathNode> nodes = sample();
		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, "999", "");

		for (PathNode n : nodes) {
			Assert.assertNull("未命中子树的节点不应被设置 idPath", n.idPath);
		}
	}

	/**
	 * 超深链：10000 层单链，验证显式栈不会触发 StackOverflowError。
	 * 绝对前缀贡献一个前导 '/'，故分隔符总数为 depth + 1。
	 */
	@Test
	public void testVeryDeepChainNoStackOverflow() {
		int depth = 10000;
		List<PathNode> chain = new ArrayList<>(depth);
		chain.add(new PathNode("0", null));
		for (int i = 1; i < depth; i++) {
			chain.add(new PathNode(String.valueOf(i), String.valueOf(i - 1)));
		}

		TreeUtil.rebuildIdPath(chain, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, "");

		PathNode leaf = find(chain, String.valueOf(depth - 1));
		// 路径形如 "/0/1/2/.../9999/"，分隔符数 = depth（节点段） + 1（前导 '/')
		String path = leaf.idPath;
		Assert.assertNotNull(path);
		int sepCount = 0;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '/') {
				sepCount++;
			}
		}
		Assert.assertEquals(depth + 1, sepCount);
	}

	/**
	 * 环处理：A->B->C->A 的环，方法应正常结束（不无限循环），且可达节点路径被正确设置。
	 */
	@Test
	public void testCycleNoInfiniteLoop() {
		List<PathNode> cycle = new ArrayList<>();
		cycle.add(new PathNode("A", null));
		cycle.add(new PathNode("B", "A"));
		cycle.add(new PathNode("C", "B"));
		// C 回指 B，形成 B->C->B 环
		cycle.add(new PathNode("B2", "C")); // 仅用于确保 C 有子节点被压栈
		cycle.add(new PathNode("D", "C"));

		TreeUtil.rebuildIdPath(cycle, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, "");

		Assert.assertEquals("/A/", find(cycle, "A").idPath);
		Assert.assertEquals("/A/B/", find(cycle, "B").idPath);
		Assert.assertEquals("/A/B/C/", find(cycle, "C").idPath);
		Assert.assertEquals("/A/B/C/D/", find(cycle, "D").idPath);
	}

	/**
	 * 重复 ID：两个 id 均为 "X" 的节点（同父），仅首个被压栈处理（文档化语义），第二个不设置路径。
	 */
	@Test
	public void testDuplicateIdFirstWins() {
		List<PathNode> nodes = new ArrayList<>();
		nodes.add(new PathNode("R", null));
		nodes.add(new PathNode("X", "R")); // 第一个 X
		nodes.add(new PathNode("X", "R")); // 重复 ID 的第二个 X

		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, "");

		// 重复 ID 中仅首个应被处理
		long setCount = nodes.stream().filter(n -> "X".equals(n.id) && n.idPath != null).count();
		Assert.assertEquals("重复 ID 中仅首个应被处理", 1L, setCount);
	}

	// =================================Contract===============================================

	/**
	 * parentIdPath 为 null 时，应规范化为根前缀 '/'（而非被 Java 拼接写成字面量 "null"）。
	 */
	@Test
	public void testNullParentIdPathNormalizedToRoot() {
		List<PathNode> nodes = sample();
		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, null);

		Assert.assertEquals("/100/", find(nodes, "100").idPath);
		Assert.assertEquals("/100/110/", find(nodes, "110").idPath);
	}

	/**
	 * parentIdPath 缺少尾部分隔符时，应自动补充分隔符，避免被直接拼接成错误路径。
	 */
	@Test
	public void testTrailingSeparatorNormalized() {
		List<PathNode> nodes = sample();
		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, "ROOT");

		Assert.assertEquals("/ROOT/100/", find(nodes, "100").idPath);
		Assert.assertEquals("/ROOT/100/110/", find(nodes, "110").idPath);
	}

	/**
	 * 空前缀应规范化为前导 '/'，得到 "/100/" 而非 "100/"（绝对路径约定）。
	 */
	@Test
	public void testEmptyPrefixNormalizedToLeadingSeparator() {
		List<PathNode> nodes = sample();
		TreeUtil.rebuildIdPath(nodes, n -> n.id, n -> n.parentId, (n, p) -> n.idPath = p, null, "");

		Assert.assertEquals("/100/", find(nodes, "100").idPath);
	}
}
