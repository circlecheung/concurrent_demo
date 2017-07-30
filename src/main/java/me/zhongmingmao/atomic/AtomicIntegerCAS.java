package me.zhongmingmao.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * 验证基于CAS实现的AtomicInteger是否并发安全
 */
public class AtomicIntegerCAS {
    private static final int THREAD_COUNT = 4;
    private static final long TASK_COUNT = 500 * 1000 * 1000;
    
    public static void main(String[] args) throws NoSuchFieldException, InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                pool.submit(() -> LongStream.range(0, TASK_COUNT)
                        .forEach(j -> counter.incrementAndGet())));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
        
        // 基于CAS实现的AtomicInteger能保证并发安全
        System.out.println(counter.get()); // 2,000,000,000 = THREAD_COUNT * TASK_COUNT
    }
}