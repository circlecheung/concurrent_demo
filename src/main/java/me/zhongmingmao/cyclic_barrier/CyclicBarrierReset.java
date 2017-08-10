package me.zhongmingmao.cyclic_barrier;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 验证还有未到达线程时，触发Reset的场景
 */
public class CyclicBarrierReset {
    private static final int THREAD_COUNT = 3;
    
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
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT - 1).forEach(value -> {
            pool.submit(awaitRunnable);
        });
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        
        // reset : 标记当代已经被打破 + 唤醒当代所有线程 + 并开启新一代
        // 被唤醒的线程将抛出BrokenBarrierException
        barrier.reset();
        /*
        输出：
        pool-1-thread-2 before barrier.await()
        pool-1-thread-1 before barrier.await()
        pool-1-thread-2 java.util.concurrent.BrokenBarrierException
        pool-1-thread-1 java.util.concurrent.BrokenBarrierException
         */
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}