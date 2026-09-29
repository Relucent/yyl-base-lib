package com.github.relucent.base.common.awt;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * RobotUtil 测试（不依赖真实显示设备）
 */
public class RobotUtilTest {

    /**
     * 不可用时 getRobot() 应抛 IllegalStateException，可用时正常返回。
     */
    @Test
    public void testGetRobotBehavior() {
        if (RobotUtil.isAvailable()) {
            assertNotNull("可用时 getRobot() 不应为 null", RobotUtil.getRobot());
        } else {
            try {
                RobotUtil.getRobot();
                fail("不可用时 getRobot() 应抛 IllegalStateException");
            } catch (IllegalStateException expected) {
                // 符合预期
            }
        }
    }
}
