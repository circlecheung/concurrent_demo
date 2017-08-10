package me.zhongmingmao.cyclic_barrier;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 验证超时的场景
 */
public class CyclicBarrierTimeoutException {
    private static final String TIMED_AWAITED_THREAD = "timed_awaited_thread";
    private static final int THREAD_COUNT = 4;
    
    private static CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
    
    private static Runnable awaitRunnable = () -> {
        try {
            log("before barrier.await()");
            if (TIMED_AWAITED_THREAD.equals(Thread.currentThread().getName())) {
                // 超时会标记当代已经被打破，并唤醒当代所有线程，最终抛出TimeoutException
                // 被唤醒的线程抛出BrokenBarrierException
                barrier.await(5, TimeUnit.SECONDS);
            } else {
                barrier.await();
            }
            
            log("after barrier.await()");
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            log(e.getClass().getCanonicalName());
        }
    };
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT - 2).forEach(value -> {
            pool.submit(awaitRunnable);
        });
        pool.shutdown();
        new Thread(awaitRunnable, TIMED_AWAITED_THREAD).start();
        /*
        输出：
        pool-1-thread-2 before barrier.await()
        pool-1-thread-1 before barrier.await()
        timed_awaited_thread before barrier.await()
        timed_awaited_thread java.util.concurrent.TimeoutException
        pool-1-thread-2 java.util.concurrent.BrokenBarrierException
        pool-1-thread-1 java.util.concurrent.BrokenBarrierException
         */
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}