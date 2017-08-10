package me.zhongmingmao.cyclic_barrier;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 验证barrierCommand抛出异常的场景
 */
public class CyclicBarrierCommandException {
    private static final int THREAD_COUNT = 3;
    
    private static CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> {
        // 最后一个到达barrier的线程后会先执行barrierCommand
        // barrierCommand抛出异常，最后一个线程唤醒其他所有线程，并抛出InterruptedException
        // 其他线程被唤醒后抛出BrokenBarrierException
        log("run barrierCommand , throw BarrierCommandException");
        throw new RuntimeException("BarrierCommandException");
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
        pool.awaitTermination(10, TimeUnit.SECONDS);
    
        // 此时barrier处于broken状态，调用await()会直接抛出BrokenBarrierException
        new Thread(awaitRunnable, "t1").start();
        TimeUnit.MILLISECONDS.sleep(100);
        // 重置barrier到初始状态
        barrier.reset();
        new Thread(awaitRunnable, "t2").start(); // 不会抛出异常
        /*
        输出：
        pool-1-thread-1 before barrier.await()
        pool-1-thread-2 before barrier.await()
        pool-1-thread-3 before barrier.await()
        pool-1-thread-3 run barrierCommand , throw BarrierCommandException
        pool-1-thread-1 java.util.concurrent.BrokenBarrierException
        pool-1-thread-2 java.util.concurrent.BrokenBarrierException
        t1 before barrier.await()
        t1 java.util.concurrent.BrokenBarrierException
        t2 before barrier.await()
         */
    }
    
    private static void log(final String msg) {
        System.out.println(String.format("%s %s", Thread.currentThread().getName(), msg));
    }
}