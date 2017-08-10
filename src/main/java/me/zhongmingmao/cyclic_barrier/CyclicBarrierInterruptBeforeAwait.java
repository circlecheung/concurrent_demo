package me.zhongmingmao.cyclic_barrier;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 验证await()前被中断线程的场景
 */
public class CyclicBarrierInterruptBeforeAwait {
    private static final int THREAD_COUNT = 3;
    private static final String SELF_INTERRUPT_THREAD_NAME = "selfInterruptThread";
    
    private static CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> {
        log("run barrierCommand");
    });
    
    private static Runnable awaitRunnable = () -> {
        try {
            if (SELF_INTERRUPT_THREAD_NAME.equals(Thread.currentThread().getName())) {
                Thread.currentThread().interrupt();
                log("self interrupt");
            }
            // 设置了中断,await()方法会标记当代已经被打破，并唤醒当代所有线程，最后抛出InterruptedException
            // 被唤醒的线程会抛出BrokenBarrierException
            log("before barrier.await()");
            barrier.await();
            log("after barrier.await()");
        } catch (InterruptedException | BrokenBarrierException e) {
            log(e.getClass().getCanonicalName());
        }
    };
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT - 1).forEach(value -> {
            pool.submit(awaitRunnable);
        });
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        
        new Thread(awaitRunnable, SELF_INTERRUPT_THREAD_NAME).start();
        /*
        输出：
        pool-1-thread-2 before barrier.await()
        pool-1-thread-1 before barrier.await()
        selfInterruptThread self interrupt
        selfInterruptThread before barrier.await()
        selfInterruptThread java.lang.InterruptedException
        pool-1-thread-2 java.util.concurrent.BrokenBarrierException
        pool-1-thread-1 java.util.concurrent.BrokenBarrierException
         */
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}