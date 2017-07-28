package me.zhongmingmao._volatile;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * volatile能保证可见性，但不保证并发安全
 */
public class ConcurrentNotSafe {
    private static final int POOL_SIZE = 4;
    
    private static volatile int i = 0;
    private static CountDownLatch countDownLatch = new CountDownLatch(1000 * 1000);
    
    private static void increase() {
        while (countDownLatch.getCount() > 0) {
            ++i;
            countDownLatch.countDown();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
        IntStream.range(0, POOL_SIZE).forEach(value -> pool.submit(ConcurrentNotSafe::increase));
        pool.shutdown();
        countDownLatch.await();
        System.out.println(i); // 890672 < 1000 * 1000
    }
}
