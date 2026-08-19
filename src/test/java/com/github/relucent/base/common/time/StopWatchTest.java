package com.github.relucent.base.common.time;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link StopWatch} 单元测试
 */
public class StopWatchTest {

    /** 短暂忙等待，替代 Thread.sleep 以减少测试耗时抖动 */
    private static void busyWait(long millis) {
        long end = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < end) {
            // 忙等待
        }
    }

    @Test
    public void testDefaultConstructor() {
        StopWatch stopWatch = new StopWatch();
        Assert.assertEquals("", stopWatch.getId());
        Assert.assertFalse(stopWatch.isRunning());
        Assert.assertNull(stopWatch.currentTaskName());
        Assert.assertEquals(0, stopWatch.getTaskCount());
        Assert.assertEquals(0L, stopWatch.getTotalTimeNanos());
    }

    @Test
    public void testIdConstructor() {
        StopWatch stopWatch = new StopWatch("任务A");
        Assert.assertEquals("任务A", stopWatch.getId());
    }

    @Test
    public void testStartStopSingleTask() {
        StopWatch stopWatch = new StopWatch("单体任务");
        Assert.assertFalse(stopWatch.isRunning());
        stopWatch.start("任务01");
        Assert.assertTrue(stopWatch.isRunning());
        Assert.assertEquals("任务01", stopWatch.currentTaskName());
        busyWait(5);
        stopWatch.stop();

        Assert.assertFalse(stopWatch.isRunning());
        Assert.assertNull(stopWatch.currentTaskName());
        Assert.assertEquals(1, stopWatch.getTaskCount());
        Assert.assertEquals("任务01", stopWatch.getLastTaskName());
        Assert.assertTrue(stopWatch.getLastTaskTimeNanos() > 0);
        Assert.assertTrue(stopWatch.getLastTaskTimeMillis() >= 0);
        Assert.assertEquals(stopWatch.getLastTaskTimeNanos(), stopWatch.getLastTaskInfo().getTimeNanos());
        Assert.assertEquals(1, stopWatch.getTaskInfo().length);
    }

    @Test
    public void testMultiTasksAccumulateTotalTime() {
        StopWatch stopWatch = new StopWatch("多任务");
        stopWatch.start("任务01");
        busyWait(5);
        stopWatch.stop();
        stopWatch.start("任务02");
        busyWait(5);
        stopWatch.stop();

        Assert.assertEquals(2, stopWatch.getTaskCount());
        Assert.assertEquals(2, stopWatch.getTaskInfo().length);
        Assert.assertEquals("任务02", stopWatch.getLastTaskName());
        // 总耗时等于各任务耗时之和
        long sum = 0;
        for (StopWatch.TaskInfo taskInfo : stopWatch.getTaskInfo()) {
            sum += taskInfo.getTimeNanos();
        }
        Assert.assertEquals(sum, stopWatch.getTotalTimeNanos());
        Assert.assertTrue(stopWatch.getTotalTimeSeconds() > 0);
    }

    @Test
    public void testStartWithoutName() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Assert.assertTrue(stopWatch.isRunning());
        Assert.assertEquals("", stopWatch.currentTaskName());
        stopWatch.stop();
        Assert.assertEquals("", stopWatch.getLastTaskName());
    }

    @Test
    public void testStartTwiceThrows() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("任务01");
        try {
            stopWatch.start("任务02");
            Assert.fail("重复 start 应当抛出 IllegalStateException");
        } catch (IllegalStateException expected) {
            // 符合预期
        } finally {
            stopWatch.stop();
        }
    }

    @Test
    public void testStopWithoutStartThrows() {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.stop();
            Assert.fail("未 start 就 stop 应当抛出 IllegalStateException");
        } catch (IllegalStateException expected) {
            // 符合预期
        }
    }

    @Test
    public void testGetLastTaskInfoWithoutTaskThrows() {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.getLastTaskInfo();
            Assert.fail("无任务时应当抛出 IllegalStateException");
        } catch (IllegalStateException expected) {
            // 符合预期
        }
        try {
            stopWatch.getLastTaskName();
            Assert.fail("无任务时应当抛出 IllegalStateException");
        } catch (IllegalStateException expected) {
            // 符合预期
        }
        try {
            stopWatch.getLastTaskTimeNanos();
            Assert.fail("无任务时应当抛出 IllegalStateException");
        } catch (IllegalStateException expected) {
            // 符合预期
        }
    }

    @Test
    public void testKeepTaskListFalse() {
        StopWatch stopWatch = new StopWatch("不保留任务");
        stopWatch.setKeepTaskList(false);
        stopWatch.start("任务01");
        busyWait(5);
        stopWatch.stop();

        Assert.assertEquals(1, stopWatch.getTaskCount());
        Assert.assertTrue(stopWatch.getTotalTimeNanos() > 0);
        try {
            stopWatch.getTaskInfo();
            Assert.fail("不保留任务时应当抛出 UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // 符合预期
        }
        Assert.assertTrue(stopWatch.prettyPrint().contains("No task info kept"));
        Assert.assertTrue(stopWatch.toString().contains("no task info kept"));
    }

    @Test
    public void testShortSummary() {
        StopWatch stopWatch = new StopWatch("汇总");
        stopWatch.start("任务01");
        busyWait(5);
        stopWatch.stop();
        String summary = stopWatch.shortSummary();
        Assert.assertTrue(summary.contains("StopWatch '汇总'"));
        Assert.assertTrue(summary.contains("running time = "));
        Assert.assertTrue(summary.contains(" ns"));
    }

    @Test
    public void testPrettyPrint() {
        StopWatch stopWatch = new StopWatch("报表");
        stopWatch.start("任务01");
        busyWait(5);
        stopWatch.stop();
        stopWatch.start("任务02");
        busyWait(5);
        stopWatch.stop();

        String text = stopWatch.prettyPrint();
        Assert.assertTrue(text.contains("StopWatch '报表'"));
        Assert.assertTrue(text.contains("Task name"));
        Assert.assertTrue(text.contains("任务01"));
        Assert.assertTrue(text.contains("任务02"));
        // 行数 = 摘要1行 + 分隔线2行 + 表头1行 + 任务2行
        Assert.assertEquals(6, text.split("\n").length);
    }

    @Test
    public void testToStringContainsTaskPercent() {
        StopWatch stopWatch = new StopWatch("字符串");
        stopWatch.start("任务01");
        busyWait(5);
        stopWatch.stop();
        String text = stopWatch.toString();
        Assert.assertTrue(text.contains("[任务01] took "));
        Assert.assertTrue(text.contains("%"));
    }

    @Test
    public void testTaskInfoTimeConversion() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("任务01");
        busyWait(5);
        stopWatch.stop();
        StopWatch.TaskInfo last = stopWatch.getLastTaskInfo();
        Assert.assertEquals("任务01", last.getTaskName());
        // 纳秒 -> 毫秒/秒 的换算关系
        Assert.assertEquals(last.getTimeNanos() / 1_000_000L, last.getTimeMillis());
        Assert.assertEquals(last.getTimeNanos() / 1_000_000_000.0, last.getTimeSeconds(), 1e-9);
    }

    @Test
    public void testStartAfterStopIsAllowed() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("任务01");
        stopWatch.stop();
        stopWatch.start("任务02");
        Assert.assertTrue(stopWatch.isRunning());
        stopWatch.stop();
        Assert.assertEquals(2, stopWatch.getTaskCount());
    }
}
