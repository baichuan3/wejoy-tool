package com.wejoy.util;


import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * <pre>
 * 
 * 延时的任务，在重启时会丢失
 * 
 * 对于其他场景，需关注
 * 
 * </pre>
 *
 */

public class DelayTaskUtil {
	public static final int MAX_QUEUE_SIZE  = 10000;
    
    private static SizedScheduledExecutorService taskScheduler = new SizedScheduledExecutorService(new ScheduledThreadPoolExecutor(getBestPoolSize()), MAX_QUEUE_SIZE);
    
    //延时读数
    public static ScheduledFuture<?> scheduleVoteCount(Runnable task, int delay) {
    	return taskScheduler.schedule(task, delay, TimeUnit.MINUTES); //
    }
    
    public static ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit timeUnit) {
        return taskScheduler.schedule(task, delay, timeUnit);
    }
    
    /**
     * 调整线程池大小
     * @param threadPoolSize
     */
    public static void resizeThreadPool(int threadPoolSize) {
        taskScheduler.setCorePoolSize(threadPoolSize);
    }

    /**
     * 返回定时任务线程池，可做更高级的应用
     * @return
     */
    public static SizedScheduledExecutorService getTaskScheduler() {
        return taskScheduler;
    }

    /**
     * 根据 Java 虚拟机可用处理器数目返回最佳的线程数。<br>
     * 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)，其中阻塞系数这里设为0.9
     */
    private static int getBestPoolSize() {
        try {
            // JVM可用处理器的个数
            final int cores = Runtime.getRuntime().availableProcessors();
            // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
            // TODO 阻塞系数是不是需要有个setter方法能让使用者自由设置呢？
            return (int)(cores / (1 - 0.9));
        }
        catch (Throwable e) {
            // 异常发生时姑且返回10个任务线程池
            return 10;
        }
    }
}
