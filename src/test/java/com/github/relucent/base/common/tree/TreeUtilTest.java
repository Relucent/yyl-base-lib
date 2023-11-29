package com.github.relucent.base.common.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TreeUtilTest {

	private List<T> data;

	@Before
	public void testBefore() {
		data = new ArrayList<T>();
		data.add(new T("100", null));
		data.add(new T("110", "100"));
		data.add(new T("111", "110"));
		data.add(new T("112", "110"));
		data.add(new T("120", "100"));
		data.add(new T("121", "120"));
		data.add(new T("122", "120"));
	}

	@Test
	public void testBuild() {
		List<N> nodes = TreeUtil.buildTree(//
				null, // 父节点ID
				data, // data
				o -> new N(o.id), // 节点适配器
				(o, d, l) -> true, // 节点过滤器
				r -> r.id, // 节点ID访问器
				r -> r.parentId, // 节点父ID访问器
				(n, c) -> n.children = c, // 子节点设置器
				(a, b) -> a.id.compareTo(b.id)// 排序比较器
		);
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
		for (N n0 : nodes) {
			idSet0.remove(n0.id);
			for (N n1 : n0.children) {
				idSet1.remove(n1.id);
				for (N n2 : n1.children) {
					idSet2.remove(n2.id);
				}
			}
		}
		Assert.assertTrue(idSet0.isEmpty());
		Assert.assertTrue(idSet1.isEmpty());
		Assert.assertTrue(idSet2.isEmpty());
	}

	private class T {
		private String id;
		private String parentId;

		public T(String id, String parentId) {
			this.id = id;
			this.parentId = parentId;
		}
	}

	private class N {
		private String id;
		private List<N> children;

		public N(String id) {
			this.id = id;
		}
	}
}
