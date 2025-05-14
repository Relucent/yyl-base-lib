package com.github.relucent.base.common.identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class UlidTest {

    @Test
    public void uniqueTest() {
        int count = 10000;
        List<String> idList = new ArrayList<>();
        Set<String> idSet = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            String ulid = Ulid.create().toString();
            idSet.add(ulid);
            idList.add(ulid);
        }
        for (int i = 1; i < count; i++) {
            Assert.assertTrue(idList.get(i - 1).compareTo(idList.get(i)) <= 0);
        }
        Assert.assertEquals(count, idSet.size());
    }
}
