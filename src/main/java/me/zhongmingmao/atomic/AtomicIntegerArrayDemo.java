package me.zhongmingmao.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

public class AtomicIntegerArrayDemo {
    private static final int THREAD_COUNT = 4;
    private static final int TASK_COUNT = 1000 * 1000;
    
    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerArray array = new AtomicIntegerArray(THREAD_COUNT);
        
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                pool.submit(() -> IntStream.range(0, TASK_COUNT)
                        .forEach(j -> array.getAndIncrement(j % THREAD_COUNT))));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
        // CAS保证并发安全
        System.out.println(array); // [1000000, 1000000, 1000000, 1000000]
    }
}
