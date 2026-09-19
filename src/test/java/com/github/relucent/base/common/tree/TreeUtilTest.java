package com.github.relucent.base.common.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TreeUtilTest {

	private List<TestRecord> data;

	@Before
	public void testBefore() {
		data = new ArrayList<TestRecord>();
		data.add(new TestRecord("100", null));
		data.add(new TestRecord("110", "100"));
		data.add(new TestRecord("111", "110"));
		data.add(new TestRecord("112", "110"));
		data.add(new TestRecord("120", "100"));
		data.add(new TestRecord("121", "120"));
		data.add(new TestRecord("122", "120"));
	}

	@Test
	public void testBuild() {

		TreeBuildRule<TestRecord, TestNode, String> rule = TreeBuildRule.<TestRecord, TestNode, String>builder()//
				.id(TestRecord::getId) // 节点ID访问器
				.parentId(TestRecord::getParentId)// 节点父ID访问器
				.adapter(e -> new TestNode(e.getId()))// 节点适配器
				.children(TestNode::setChildren)// 子节点设置器
				.comparator((a, b) -> a.id.compareTo(b.id))// 排序比较器
				.build();

		List<TestNode> nodes = TreeUtil.buildTree(data, null, rule);

		Set<String> idSet0 = new HashSet<>();
		idSet0.add("100");
		Set<String> idSet1 = new HashSet<>();
		idSet1.add("110");
		idSet1.add("120");
		Set<String> idSet2 = new HashSet<>();
		idSet2.add("111");
		idSet2.add("112");
		idSet2.add("121");
		idSet2.add("122");
		for (TestNode n0 : nodes) {
			idSet0.remove(n0.id);
			for (TestNode n1 : n0.children) {
				idSet1.remove(n1.id);
				for (TestNode n2 : n1.children) {
					idSet2.remove(n2.id);
				}
			}
		}
		Assert.assertTrue(idSet0.isEmpty());
		Assert.assertTrue(idSet1.isEmpty());
		Assert.assertTrue(idSet2.isEmpty());
	}

	@SuppressWarnings("unused")
	private class TestRecord {

		private String id;
		private String parentId;

		public TestRecord(String id, String parentId) {
			this.id = id;
			this.parentId = parentId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
	}

	@SuppressWarnings("unused")
	private class TestNode {

		private String id;
		private List<TestNode> children;

		public TestNode(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<TestNode> getChildren() {
			return children;
		}

		public void setChildren(List<TestNode> children) {
			this.children = children;
		}
	}
}
