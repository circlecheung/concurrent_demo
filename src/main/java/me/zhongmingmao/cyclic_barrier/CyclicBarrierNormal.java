package me.zhongmingmao.cyclic_barrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * CyclicBarrier正常流程
 */
public class CyclicBarrierNormal {
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
        IntStream.range(0, THREAD_COUNT).forEach(value -> {
            pool.submit(awaitRunnable);
        });
        pool.shutdown();
        /*
        输出：
        pool-1-thread-1 before barrier.await()
        pool-1-thread-3 before barrier.await()
        pool-1-thread-2 before barrier.await()
        pool-1-thread-2 run barrierCommand
        pool-1-thread-1 after barrier.await()
        pool-1-thread-3 after barrier.await()
        pool-1-thread-2 after barrier.await()
         */
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}