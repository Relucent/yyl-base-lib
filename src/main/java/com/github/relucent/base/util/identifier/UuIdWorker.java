package com.github.relucent.base.util.identifier;

import java.util.UUID;

/**
 * ID生成器<br>
 */
public class UuIdWorker {

    /**
     * 获得32位UUID
     * @return UUID
     */
    public static String nextId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
