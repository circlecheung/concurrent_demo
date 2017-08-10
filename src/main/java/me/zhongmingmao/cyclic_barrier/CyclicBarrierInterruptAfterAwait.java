package me.zhongmingmao.cyclic_barrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 验证await()后被中断线程的场景
 */
public class CyclicBarrierInterruptAfterAwait {
    private static final int THREAD_COUNT = 4;
    
    private static CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> {
        log("run barrierCommand");
    });
    
    private static Runnable awaitRunnable = () -> {
        try {
            log("before barrier.await()");
            barrier.await();
            log("after barrier.await()");
        } catch (InterruptedException | BrokenBarrierException e) {
            log(e.getClass().getCanonicalName());
        }
    };
    
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(awaitRunnable, "t1");
        Thread t2 = new Thread(awaitRunnable, "t2");
        Thread t3 = new Thread(awaitRunnable, "t3");
        
        t1.start();
        t2.start();
        t3.start();
        TimeUnit.MILLISECONDS.sleep(100);
        // t3被中断，唤醒其他线程，最后抛出InterruptedException
        // 被唤醒的线程抛出BrokenBarrierException
        t3.interrupt();
        /*
        输出：
        t1 before barrier.await()
        t3 before barrier.await()
        t2 before barrier.await()
        t2 java.util.concurrent.BrokenBarrierException
        t1 java.util.concurrent.BrokenBarrierException
        t3 java.lang.InterruptedException
         */
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}