package com.github.relucent.base.common.identifier;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SnowflakeIdWorkerTest {
    @Test
    public void uniqueTest() {
        SnowflakeIdWorker snowflake = SnowflakeIdWorker.DEFAULT;
        int count = 10000;
        final Set<Long> idSet = new HashSet<>(count);
        for (int i = 0; i < 10000; i++) {
            idSet.add(snowflake.nextId());
        }
        Assert.assertEquals(count, idSet.size());
    }
}
