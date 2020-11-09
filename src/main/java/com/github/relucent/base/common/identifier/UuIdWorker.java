package com.github.relucent.base.common.identifier;

/**
 * ID生成器<br>
 */
public class UuIdWorker {

    /**
     * 获得32位UUID
     * @return UUID
     */
    public static String nextId() {
        return UUID32.randomUUID().toString();
    }
}
