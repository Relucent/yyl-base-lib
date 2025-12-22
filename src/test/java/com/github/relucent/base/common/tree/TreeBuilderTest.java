package com.github.relucent.base.common.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

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
                .cyclePolicy(TreeBuilder.CyclePolicy.ERROR)//
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
}
