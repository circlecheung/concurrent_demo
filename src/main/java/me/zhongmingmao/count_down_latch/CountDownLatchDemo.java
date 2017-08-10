package me.zhongmingmao.count_down_latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * CountDownLatch的简单使用
 */
public class CountDownLatchDemo {
    
    private static final int THREAD_COUNT = 4;
    
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        
        ExecutorService mPool = Executors.newFixedThreadPool(THREAD_COUNT);
        
        IntStream.range(0, THREAD_COUNT).forEach(value ->
                mPool.submit(() -> {
                    doTask();
                    log("finished!");
                    countDownLatch.countDown(); // state-1
                }));
        
        ExecutorService nPool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(value ->
                nPool.submit(() -> {
                    try {
                        countDownLatch.await(); // 等待state减少到0
                        log("started!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
        
        mPool.shutdown();
        nPool.shutdown();
    
        /*
         输出：
            pool-1-thread-2 finished!
            pool-1-thread-3 finished!
            pool-1-thread-4 finished!
            pool-2-thread-2 started!
            pool-2-thread-3 started!
            pool-2-thread-1 started!
            pool-2-thread-4 started!
         */
    }
    
    private static void doTask() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}