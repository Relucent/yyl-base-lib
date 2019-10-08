package com.github.relucent.base.util.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CaseInsensitiveKeyMapTest {

    private Map<String, String> sample = new CaseInsensitiveKeyMap<>();

    @Before
    public void before() {
        sample.put("Host", "localhost");
        sample.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.");
        sample.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        sample.put("Accept-Language", "zh-cn,zh;q=0.5");
        sample.put("Accept-Encoding", "gzip,deflate");
        sample.put("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        sample.put("Connection", "close");
    }

    @Test
    public void testGet() {
        for (String key : sample.keySet()) {
            String lowerKey = key.toLowerCase();
            String upperKey = key.toUpperCase();
            Assert.assertEquals(sample.get(lowerKey), sample.get(upperKey));
        }
    }

    @Test
    public void testRemove() {
        List<String> keys = new ArrayList<>();
        keys.addAll(sample.keySet());
        for (String key : keys) {
            sample.remove(key.toLowerCase());
        }
        Assert.assertTrue(sample.isEmpty());
    }

    @Test
    public void testEntrySet() {
        for (Iterator<Entry<String, String>> it = sample.entrySet().iterator(); it.hasNext();) {
            it.next();
        }
    }

    @Test
    public void testNull() {
        sample.put(null, null);
        sample.remove(null);
        sample.get(null);
    }
}
