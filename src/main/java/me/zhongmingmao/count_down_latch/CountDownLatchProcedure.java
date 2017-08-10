package me.zhongmingmao.count_down_latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 简述CountDownLatch的工作过程
 * 为「并发 - JUC - CountDownLatch - 源码剖析」的配套代码
 */
public class CountDownLatchProcedure {
    
    private static final int THREAD_COUNT = 4;
    
    private static CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
    
    private static Runnable awaitRunnable = () -> {
        try {
            log("start!");
            countDownLatch.await();
            log("continue!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };
    
    public static void main(String[] args) {
        IntStream.range(0, THREAD_COUNT).forEach(i -> {
            new Thread(awaitRunnable, String.format("t%s", i + 1)).start();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        IntStream.range(0, THREAD_COUNT).forEach(i -> countDownLatch.countDown());
        
        /*
         输出：
        t1 start!
        t2 start!
        t3 start!
        t4 start!
        t1 continue!
        t2 continue!
        t4 continue!
        t3 continue!
         */
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}